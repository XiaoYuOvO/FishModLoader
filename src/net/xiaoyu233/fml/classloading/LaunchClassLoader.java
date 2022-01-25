package net.xiaoyu233.fml.classloading;

import net.xiaoyu233.fml.asm.IClassNameTransformer;
import net.xiaoyu233.fml.asm.IClassTransformer;
import net.xiaoyu233.fml.config.Configs;
import net.xiaoyu233.fml.util.LogWrapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class LaunchClassLoader extends URLClassLoader {
   public static final int BUFFER_SIZE = 4096;
   private final List<URL> sources;
   private final ClassLoader parent = this.getClass().getClassLoader();
   private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("legacy.debugClassLoading", "false")) || Configs.Debug.debug.get();

   static {
      DEBUG_FINER = DEBUG && (Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingFiner", "false")) || Configs.Debug.printClassloadInfo.get());
      DEBUG_SAVE = DEBUG && (Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingSave", "true")) || Configs.Debug.DumpClass.dumpClass.get());
      tempFolder = null;
   }

   private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();
   private final Set<String> classLoaderExceptions = new HashSet<>();
   private final Set<String> invalidClasses = new HashSet<>(1000);
   private final ThreadLocal<byte[]> loadBuffer = new ThreadLocal<>();
   private final Set<String> negativeResourceCache = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private IClassNameTransformer renameTransformer;
   private final Map<String, byte[]> resourceCache = new ConcurrentHashMap<>(1000);
   private static final String[] RESERVED_NAMES = new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
   private final Set<String> transformerExceptions = new HashSet<>();
   private static final boolean DEBUG_FINER;
   private static final boolean DEBUG_SAVE;
   private static File tempFolder;
   private final List<IClassTransformer> transformers = new ArrayList<>(2);

   public void registerTransformer(IClassTransformer transformer) {
      try {
         this.transformers.add(transformer);
         if (this.renameTransformer == null && transformer instanceof IClassNameTransformer) {
            this.renameTransformer = (IClassNameTransformer)transformer;
         }
      } catch (Exception var3) {
         LogWrapper.log(Level.ERROR, var3, "A critical problem occurred registering the ASM transformer class %s", transformer);
      }

   }

   public void registerTransformer(String transformerClassName) {
      try {
         IClassTransformer transformer = (IClassTransformer)this.parent.loadClass(transformerClassName).newInstance();
         this.transformers.add(transformer);
         if (this.renameTransformer == null && transformer instanceof IClassNameTransformer) {
            this.renameTransformer = (IClassNameTransformer)transformer;
         }
      } catch (Exception var3) {
         LogWrapper.log(Level.ERROR, var3, "A critical problem occurred registering the ASM transformer class %s", transformerClassName);
      }

   }

   public Class<?> findClass(final String name) throws ClassNotFoundException {
      if (invalidClasses.contains(name)) {
         throw new ClassNotFoundException(name);
      }

      for (final String exception : classLoaderExceptions) {
         if (name.startsWith(exception)) {
            return parent.loadClass(name);
         }
      }

      if (cachedClasses.containsKey(name)) {
         return cachedClasses.get(name);
      }

      for (final String exception : transformerExceptions) {
         if (name.startsWith(exception)) {
            try {
               final Class<?> clazz = super.findClass(name);
               cachedClasses.put(name, clazz);
               return clazz;
            } catch (ClassNotFoundException e) {
               invalidClasses.add(name);
               throw e;
            }
         }
      }

      try {
         final String transformedName = transformName(name.replace(".","/"));
         if (cachedClasses.containsKey(transformedName)) {
            return cachedClasses.get(transformedName);
         }

         final String untransformedName = untransformName(name.replace(".","/"));

         final int lastDot = untransformedName.lastIndexOf('.');
         final String packageName = lastDot == -1 ? "" : untransformedName.substring(0, lastDot);
         final String fileName = untransformedName.replace('.', '/').concat(".class");
         URLConnection urlConnection = findCodeSourceConnectionFor(fileName);

         CodeSigner[] signers = null;

         if (lastDot > -1 && !untransformedName.startsWith("net.minecraft.")) {
            if (urlConnection instanceof JarURLConnection) {
               final JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
               final JarFile jarFile = jarURLConnection.getJarFile();

               if (jarFile != null && jarFile.getManifest() != null) {
                  final Manifest manifest = jarFile.getManifest();
                  final JarEntry entry = jarFile.getJarEntry(fileName);

                  Package pkg = getPackage(packageName);
                  getClassBytes(untransformedName);
                  signers = entry.getCodeSigners();
                  if (pkg == null) {
                     pkg = definePackage(packageName, manifest, jarURLConnection.getJarFileURL());
                  } else {
                     if (pkg.isSealed() && !pkg.isSealed(jarURLConnection.getJarFileURL())) {
                        LogWrapper.severe("The jar file %s is trying to seal already secured path %s", jarFile.getName(), packageName);
                     } else if (isSealed(packageName, manifest)) {
                        LogWrapper.severe("The jar file %s has a security seal for path %s, but that path is defined and not secure", jarFile.getName(), packageName);
                     }
                  }
               }
            } else {
               Package pkg = getPackage(packageName);
               if (pkg == null) {
                  pkg = definePackage(packageName, null, null, null, null, null, null, null);
               } else if (pkg.isSealed()) {
                  LogWrapper.severe("The URL %s is defining elements for sealed path %s", urlConnection.getURL(), packageName);
               }
            }
         }

         final byte[] transformedClass = runTransformers(untransformedName, transformedName, getClassBytes(untransformedName));
         if (DEBUG_SAVE && transformedName.startsWith("net.minecraft.")) {
            saveTransformedClass(transformedClass, transformedName);
         }

         final CodeSource codeSource = urlConnection == null ? null : new CodeSource(urlConnection.getURL(), signers);
         final Class<?> clazz = defineClass(transformedName, transformedClass, 0, transformedClass.length, codeSource);
         cachedClasses.put(transformedName, clazz);
         return clazz;
      } catch (Throwable e) {
         invalidClasses.add(name);
         if (DEBUG) {
            LogWrapper.log(Level.TRACE, e, "Exception encountered attempting classloading of %s", name);
            LogManager.getLogger("LaunchWrapper").log(Level.ERROR, "Exception encountered attempting classloading of %s", e);
         }
         throw new ClassNotFoundException(name, e);
      }
   }

   private void saveTransformedClass(byte[] data, String transformedName) {
      if (tempFolder != null) {
         File outFile = new File(tempFolder, transformedName.replace('.', File.separatorChar) + ".class");
         File outDir = outFile.getParentFile();
         if (!outDir.exists()) {
            outDir.mkdirs();
         }

         if (outFile.exists()) {
            outFile.delete();
         }

         try {
            OutputStream output = new FileOutputStream(outFile);
            output.write(data);
            output.close();
         } catch (IOException var6) {
            LogWrapper.log(Level.WARN, var6, "Could not save transformed class \"%s\"", transformedName);
         }

      }
   }

   public String untransformName(String name) {
      return this.renameTransformer != null ? this.renameTransformer.unmapClassName(name) : name;
   }

   public String transformName(String name) {
      return this.renameTransformer != null ? this.renameTransformer.remapClassName(name) : name;
   }

   private boolean isSealed(String path, Manifest manifest) {
      Attributes attributes = manifest.getAttributes(path);
      String sealed = null;
      if (attributes != null) {
         sealed = attributes.getValue(Name.SEALED);
      }

      if (sealed == null) {
         attributes = manifest.getMainAttributes();
         if (attributes != null) {
            sealed = attributes.getValue(Name.SEALED);
         }
      }

      return "true".equalsIgnoreCase(sealed);
   }

   private URLConnection findCodeSourceConnectionFor(String name) {
      URL resource = this.findResource(name);
      if (resource != null) {
         try {
            return resource.openConnection();
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }
      } else {
         return null;
      }
   }

   public LaunchClassLoader(URL[] sources) {
      super(sources, null);
      this.sources = new ArrayList<>(Arrays.asList(sources));
      this.addClassLoaderExclusion("java.");
      this.addClassLoaderExclusion("sun.");
      this.addClassLoaderExclusion("org.lwjgl.");
      this.addClassLoaderExclusion("org.apache.logging.");
      this.addClassLoaderExclusion("net.minecraft.launchwrapper.");
      this.addTransformerExclusion("javax.");
      this.addTransformerExclusion("argo.");
      this.addTransformerExclusion("org.objectweb.asm.");
      this.addTransformerExclusion("com.google.common.");
      this.addTransformerExclusion("org.bouncycastle.");
      this.addTransformerExclusion("net.minecraft.launchwrapper.injector.");
      if (DEBUG_SAVE) {
         tempFolder = Configs.Debug.DumpClass.dumpPath.get();
         LogWrapper.info("DEBUG_SAVE Enabled, saving all classes to \"%s\"", tempFolder.getAbsolutePath().replace('\\', '/'));
         tempFolder.mkdirs();
      }

   }

   public void addURL(URL url) {
      super.addURL(url);
      this.sources.add(url);
   }

   public List<URL> getSources() {
      return this.sources;
   }

   private byte[] readFully(InputStream stream) {
      try {
         byte[] buffer = this.getOrCreateBuffer();
         int totalLength = 0;

         int read;
         byte[] newBuffer;
         while((read = stream.read(buffer, totalLength, buffer.length - totalLength)) != -1) {
            totalLength += read;
            if (totalLength >= buffer.length - 1) {
               newBuffer = new byte[buffer.length + 4096];
               System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
               buffer = newBuffer;
            }
         }

         newBuffer = new byte[totalLength];
         System.arraycopy(buffer, 0, newBuffer, 0, totalLength);
         return newBuffer;
      } catch (Throwable var6) {
         LogWrapper.log(Level.WARN, var6, "Problem loading class");
         return new byte[0];
      }
   }

   private byte[] getOrCreateBuffer() {
      byte[] buffer = this.loadBuffer.get();
      if (buffer == null) {
         this.loadBuffer.set(new byte[4096]);
         buffer = this.loadBuffer.get();
      }

      return buffer;
   }

   public List<IClassTransformer> getTransformers() {
      return Collections.unmodifiableList(this.transformers);
   }

   public IClassNameTransformer getRenameTransformer() {
      return renameTransformer;
   }

   public void addClassLoaderExclusion(String toExclude) {
      this.classLoaderExceptions.add(toExclude);
   }

   public void addTransformerExclusion(String toExclude) {
      this.transformerExceptions.add(toExclude);
   }

   public byte[] getClassBytes(String name) throws IOException {
      if (this.negativeResourceCache.contains(name)) {
         return null;
      } else if (this.resourceCache.containsKey(name)) {
         return this.resourceCache.get(name);
      } else {
         String reservedName;
         byte[] data;
         if (name.indexOf(46) == -1) {

            for (String s : RESERVED_NAMES) {
               reservedName = s;
               if (name.toUpperCase(Locale.ENGLISH).startsWith(reservedName)) {
                  data = this.getClassBytes("_" + name);
                  if (data != null) {
                     this.resourceCache.put(name, data);
                     return data;
                  }
               }
            }
         }

         InputStream classStream = null;

         try {
            String resourcePath = name.replace('.', '/').concat(".class");
            URL classResource = this.findResource(resourcePath);
            if (classResource == null) {
               if (DEBUG) {
                  LogWrapper.finest("Failed to find class resource %s", resourcePath);
               }

               this.negativeResourceCache.add(name);
               return null;
            }

            classStream = classResource.openStream();
            if (DEBUG) {
               LogWrapper.finest("Loading class %s from resource %s", name, classResource.toString());
            }

            data = this.readFully(classStream);
            this.resourceCache.put(name, data);
         } finally {
            closeSilently(classStream);
         }

         return data;
      }
   }

   private static void closeSilently(Closeable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
         }
      }

   }

   public void clearNegativeEntries(Set<String> entriesToClear) {
      this.negativeResourceCache.removeAll(entriesToClear);
   }

   public byte[] runTransformers(String name, String transformedName, byte[] basicClass) {
      Iterator<IClassTransformer> var4;
      IClassTransformer transformer;
      if (DEBUG_FINER) {
         LogWrapper.finest("Beginning transform of {%s (%s)} Start Length: %d", name, transformedName, basicClass == null ? 0 : basicClass.length);
         var4 = this.transformers.iterator();

         while(var4.hasNext()) {
            transformer = var4.next();
            String transName = transformer.getClass().getName();
            LogWrapper.finest("Before Transformer {%s (%s)} %s: %d", name, transformedName, transName, basicClass == null ? 0 : basicClass.length);
            basicClass = transformer.transform(name, transformedName, basicClass);
            LogWrapper.finest("After  Transformer {%s (%s)} %s: %d", name, transformedName, transName, basicClass == null ? 0 : basicClass.length);
         }

         LogWrapper.finest("Ending transform of {%s (%s)} Start Length: %d", name, transformedName, basicClass == null ? 0 : basicClass.length);
      } else {
         for(var4 = this.transformers.iterator(); var4.hasNext(); basicClass = transformer.transform(name, transformedName, basicClass)) {
            transformer = var4.next();
         }
      }

      return basicClass;
   }
}
