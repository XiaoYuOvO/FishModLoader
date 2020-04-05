package net.xiaoyu233.fml.relaunch;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.ModsWalker;
import net.xiaoyu233.fml.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class Launch {
    public static String mainClass;
    public static void launch(String mainClass,String[] args) throws IOException {
        Launch.mainClass = mainClass;
        ModsWalker.LoadConfig var4;
        if (FishModLoader.isServer()){
            extractSource();
            var4 = ModsWalker.getBuilder("source.jar");
        }else {
            String jarPath = System.getProperty("minecraft.client.jar");
            if (jarPath != null){
                String jar = jarPath.substring(jarPath.indexOf("\\")+1);
                var4 = ModsWalker.getBuilder(jar);
            }else {
                var4 = ModsWalker.getBuilder("./versions/1.6.4-MITE/1.6.4-MITE.jar");
            }
        }
        FishModLoader.loadConfig();
        try {
            var4.setDebug(FishModLoader.config.get("debug"));
        }catch (Exception e) {
            e.printStackTrace();
            var4.setDebug(false);
        }
        var4.setModFolder(new File( new File(System.getProperty("user.dir")), "coremods"));
        var4.build();
        try {
            Class<?> var6 = Thread.currentThread().getContextClassLoader().loadClass(mainClass);
            var6.getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException var7) {
            var7.printStackTrace();
        }
    }

    private static void extractSource() throws IOException {
        File sourceFile = new File("source.jar");
        if (!sourceFile.exists()){
            Utils.logInfoConsole("Extracting Source classes...");
            Utils.createJar("source");
            JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(sourceFile));
            for (Map.Entry<String, InputStream> stringInputStreamEntry : Utils.getInternalClassesFromJar(
                    name -> name.endsWith(".class") && (!name.contains("/") || name.contains(
                            "net/minecraft"))).entrySet()) {
                outputStream.putNextEntry(new JarEntry(stringInputStreamEntry.getKey()));
                Utils.copy(stringInputStreamEntry.getValue(),outputStream);
                stringInputStreamEntry.getValue().close();
            }
            outputStream.flush();
            outputStream.close();
            Utils.logInfoConsole("Source classes extract successfully");
        }

    }
}
