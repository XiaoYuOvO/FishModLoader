package net.xiaoyu233.fml.relaunch.server;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.ModsWalker;
import net.xiaoyu233.fml.classloading.ModClassLoader;
import net.xiaoyu233.fml.config.Config;
import net.xiaoyu233.fml.config.JsonConfig;
import net.xiaoyu233.fml.relaunch.Launch;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] var0) {
        FishModLoader.setIsServer(true);
        Launch.launch("net.minecraft.server.MinecraftServer",var0);
    }
}
