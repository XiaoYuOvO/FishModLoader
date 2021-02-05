package net.xiaoyu233.fml.relaunch;

import com.google.common.collect.Lists;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.classloading.LaunchClassLoader;
import net.xiaoyu233.fml.mapping.Remapping;
import net.xiaoyu233.fml.mapping.Renamer;
import net.xiaoyu233.fml.mixin.service.ClassProvider;
import net.xiaoyu233.fml.util.LogProxy;
import net.xiaoyu233.fml.util.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Launch {
   public static String mainClass;
   public static Map<String, Object> blackboard = new HashMap();
   public static LaunchClassLoader classLoader;
   public static String minecraftHome;

   public static void launch(String mainClass, String[] args) throws IOException {
      classLoader = new LaunchClassLoader(ClassProvider.getSystemClassPathURLs());

      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.contains("gameDir")) {
            String gamePath = args[i + 1];
            System.out.println(gamePath);
            System.setProperty("user.dir", gamePath);
            minecraftHome = gamePath;
         }
      }

      MixinBootstrap.init();
      MixinBootstrap.getPlatform().prepare(CommandLineOptions.of(Lists.newArrayList(new String[]{"/net.xiaoyu233.fml.json"})));
      MixinEnvironment preinit = MixinEnvironment.getEnvironment(Phase.PREINIT);
      preinit.setSide(FishModLoader.isServer() ? Side.SERVER : Side.CLIENT);
      Remapping remapping = new Remapping();
      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/class.mapping"), Remapping.MappingType.CLASS);
      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/method.mapping"), Remapping.MappingType.METHOD);
      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/field.mapping"), Remapping.MappingType.FIELD);
      MixinEnvironment.getCurrentEnvironment().getRemappers().add(remapping);
      classLoader.registerTransformer(new Renamer(remapping));
      LogProxy.proxySysout();
      LogProxy.proxySyserr();

      try {
         Class<?> var6 = classLoader.loadClass(mainClass);
         String path = var6.getProtectionDomain().getCodeSource().getLocation().getPath().split("!")[0];
         path = URLDecoder.decode(path, "UTF-8").replace("file:/", "");
         if (path.endsWith(".jar")) {
            JarFile minecraftJar = new JarFile(path);
            Enumeration entries = minecraftJar.entries();

            while(entries.hasMoreElements()) {
               JarEntry entry = (JarEntry)entries.nextElement();
               if (entry.getName().endsWith(".class")) {
                  ClassReader reader = new ClassReader(minecraftJar.getInputStream(entry));
                  ClassNode classNode = new ClassNode();
                  reader.accept(classNode, 0);
                  remapping.addSuperclassMapping(classNode.name, classNode.superName);
                  remapping.addInterfaceMap(classNode.name, classNode.interfaces);
               }
            }
         }

         FishModLoader.logger.info("Starting Minecraft");
         var6.getMethod("main", String[].class).invoke(null, args);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException var11) {
         var11.printStackTrace();
      }

   }

   private static void extractSource() throws IOException {
      File sourceFile = new File("source.jar");
      if (!sourceFile.exists()) {
         Utils.logInfoConsole("Extracting Source classes...");
         Utils.createJar("source");
         JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(sourceFile));
         Iterator var2 = Utils.getInternalClassesFromJar((name) -> {
            return name.endsWith(".class") && (!name.contains("/") || name.contains("net/minecraft"));
         }).entrySet().iterator();

         while(var2.hasNext()) {
            Entry<String, InputStream> stringInputStreamEntry = (Entry)var2.next();
            outputStream.putNextEntry(new JarEntry(stringInputStreamEntry.getKey()));
            Utils.copy(stringInputStreamEntry.getValue(), outputStream);
            stringInputStreamEntry.getValue().close();
         }

         outputStream.flush();
         outputStream.close();
         Utils.logInfoConsole("Source classes extract successfully");
      }

   }
}
