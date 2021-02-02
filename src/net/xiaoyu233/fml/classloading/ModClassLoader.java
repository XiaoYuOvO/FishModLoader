package net.xiaoyu233.fml.classloading;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.Mapping;
import net.xiaoyu233.fml.asm.ModsWalker;
import net.xiaoyu233.fml.asm.Transformer;
import net.xiaoyu233.fml.util.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ModClassLoader extends URLClassLoader {
    private static final ClassLoader parent = ModsWalker.class.getClassLoader();
    private static boolean dumpClass;
    private static String dumpFilter;
    private static String dumpPath;
    private static boolean printClassLoadInfo;
    private static boolean cleanClassImplementation;
    private static boolean filterOutput;
    private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();
    private final Transformer transformManager;
    private final Set<String> unfoundClasses = new HashSet<>();
    private final URL fileUrl;


    static {
        if (FishModLoader.config != null && FishModLoader.config.has("dumpClass")) {
            dumpClass = FishModLoader.config.get("dumpClass");
            if (dumpClass) {
                dumpPath = FishModLoader.config.get("dumpPath");
                dumpFilter = FishModLoader.config.get("dumpFilter");
                printClassLoadInfo = FishModLoader.config.get("printClassLoadInfo");
                cleanClassImplementation = FishModLoader.config.get("cleanClassImplementation");
                filterOutput = FishModLoader.config.get("filterOutput");
            }
        }
    }

    public ModClassLoader(File var1, Transformer var2) throws IOException {
        super(new URL[0], parent);
        this.fileUrl = var1.toURI().toURL();
        this.addURL(this.fileUrl);
        this.transformManager = var2;
        this.addClassToLoader(ModClassLoader.class);
    }

    public void addURL(File var1) {
        try {
            this.addURL(var1.toURI().toURL());
        } catch (MalformedURLException var3) {
            var3.printStackTrace();
        }

    }

    public void addURL(URL var1) {
        super.addURL(var1);
    }

    public Class<?> defineClass1(String name, byte[] b, int off, int len) {
        return super.defineClass(name, b, off, len);
    }

    protected Class<?> findClass(String var1) throws ClassNotFoundException {
        if (this.unfoundClasses.contains(var1)) {
            throw new ClassNotFoundException(var1);
        }
//        if (var1.contains("net.xiaoyu233")){
//            String var2 = Mapping.getOriginalName(var1).replace('.', '/').concat(".class");
//            URL var4 = this.findResource(var2);
//            System.out.println(var4);
//            return parent.loadClass(var1);
//        }
        else{
            try {
                if (!var1.contains(".")) {
                    var1 = Mapping.getClassMapName(var1);
                }
                if (this.cachedClasses.containsKey(var1)) {
                    return this.cachedClasses.get(var1);
                }
                String var2 = Mapping.getOriginalName(var1).replace('.', '/').concat(".class");
                Class<?> var3 = null;
                URL var4 = this.findResource(var2);
                if (var4 == null) {
                    var3 = parent.loadClass(var1);
                } else {
                    byte[] var5 = new byte[0];

                    try {
                        InputStream var6 = var4.openStream();
                        Throwable var31 = null;

                        try {
                            var5 = this.transformManager.transform(Utils.readAllBytes(var6));
                            if (printClassLoadInfo) {
                                System.out.println(var1);
                            }

                            if (dumpClass && var1.contains(dumpFilter) && (!filterOutput || transformManager.hasBeenTransformed(var1.replace(".", "/")))) {
                                String var32 = dumpPath + "/" + var1.replace(".", "\\");
                                int var9 = var32.lastIndexOf("\\");
                                File var10 = new File(var32.substring(0, var9));
                                var10.mkdirs();
                                FileOutputStream var11 = new FileOutputStream(var32 + ".class");
                                byte[] out = var5;
                                if (cleanClassImplementation){
                                    ClassNode node = new ClassNode();
                                    ClassReader reader = new ClassReader(var5);
                                    reader.accept(node,0);
                                    if (node.interfaces != null){
                                        node.interfaces.removeIf(s -> !s.startsWith("net/minecraft"));
                                    }
                                    ClassWriter writer = new ClassWriter(0);
                                    node.accept(writer);
                                    out = writer.toByteArray();
                                }
                                var11.write(out);
                                var11.close();
                            }

                            var3 = this.defineClass(var1, var5, 0, var5.length);
                        } catch (Throwable var26) {
                            var31 = var26;
                            throw var26;
                        } finally {
                            if (var6 != null) {
                                if (var31 != null) {
                                    try {
                                        var6.close();
                                    } catch (Throwable var25) {
                                        var31.addSuppressed(var25);
                                    }
                                } else {
                                    var6.close();
                                }
                            }

                        }
                    } catch (IOException var28) {
                        throw new ClassNotFoundException(var1, var28);
                    } catch (ClassFormatError var29) {
                        if (var5.length != 0) {
                            File var7 = new File("ErrorClass.class");

                            try {
                                var7.createNewFile();
                                FileOutputStream var8 = new FileOutputStream(var7);
                                var8.write(var5);
                                var8.close();
                            } catch (IOException var23) {
                                var23.printStackTrace();
                            }
                        }

                        var29.printStackTrace();
                    }
                }

                if (var3 == null) {
                    throw new ClassNotFoundException(var1);
                } else {
                    this.cachedClasses.put(var1, var3);
                    return var3;
                }
            } catch (ClassNotFoundException var30) {
                this.unfoundClasses.add(var1);
                throw var30;
            }
        }
    }

    public void addClassToLoader(Class<?> c){
        this.cachedClasses.put(c.getName(),c);
    }

    public Class<?> loadClass(String var1) throws ClassNotFoundException {
        return this.findClass(var1);
    }
}
