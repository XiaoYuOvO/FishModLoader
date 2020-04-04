package net.xiaoyu233.fml.relaunch;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.ModsWalker;
import net.xiaoyu233.fml.classloading.ModClassLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class Launch {
    public static String mainClass;
    public static void launch(String mainClass,String[] args){
        Launch.mainClass = mainClass;
        ModsWalker.LoadConfig var4 = ModsWalker.getBuilder(FishModLoader.config.get("jarPath"));
        var4.setDebug(FishModLoader.config.get("debug"));
        var4.setModFolder(new File( new File(System.getProperty("user.dir")), "coremods"));
        var4.build();
        try {
            Class<?> var6 = Thread.currentThread().getContextClassLoader().loadClass(mainClass);
            var6.getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException var7) {
            var7.printStackTrace();
        }
    }
}
