package net.xiaoyu233.fml.relaunch;

import net.xiaoyu233.fml.classloading.KnotClassLoaderInterface;

public class LaunchClassBlocker {
    public static void blockClass(KnotClassLoaderInterface classLoaderInterface){
        classLoaderInterface.blockClassPrefix("net.xiaoyu233.fml");
        classLoaderInterface.blockClassPrefix("org.apache.logging");
        classLoaderInterface.blockClassPrefix("com.google.common");
        classLoaderInterface.blockClassPrefix("org.spongepowered.asm");
        classLoaderInterface.blockClassPrefix("org.objectweb.asm");
        classLoaderInterface.blockClassPrefix("com.google.gson");
        classLoaderInterface.blockClassPrefix("net.fabricmc.loader");
        classLoaderInterface.blockClassPrefix("net.fabricmc.api");
        classLoaderInterface.blockClassPrefix("com.chocohead.mm");
        classLoaderInterface.whitelistClassPrefix("net.xiaoyu233.fml.reload.util");
        classLoaderInterface.whitelistClassPrefix("net.xiaoyu233.fml.reload.event");
        classLoaderInterface.whitelistClassPrefix("net.xiaoyu233.fml.api");
    }
}
