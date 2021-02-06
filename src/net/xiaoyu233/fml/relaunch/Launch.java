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
        FishModLoader.loadConfig();
        Launch.mainClass = mainClass;
        ModsWalker.LoadConfig var4;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.contains("gameDir")){
                String gamePath = args[i+1];
                System.out.println(gamePath);
                System.setProperty("user.dir",gamePath);
            }

        }
        if (FishModLoader.isServer()){
            extractSource();
            var4 = ModsWalker.getBuilder("source.jar");
        }else {
            String userPath = System.getProperty("user.dir");
            if (userPath != null){
                var4 = ModsWalker.getBuilder(new File(new File(userPath),"./versions/1.6.4-MITE/1.6.4-MITE.jar").getCanonicalPath());
            }else {
                var4 = ModsWalker.getBuilder("./versions/1.6.4-MITE/1.6.4-MITE.jar");
            }
        }
        try {
            var4.setDebug(FishModLoader.config.get("debug"));
        }catch (Exception e) {
            e.printStackTrace();
            var4.setDebug(false);
        }
        var4.setModFolder(new File("coremods"));
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
