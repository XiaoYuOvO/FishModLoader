package net.xiaoyu233.fml.relaunch.client;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.Mapping;
import net.xiaoyu233.fml.asm.ModsWalker;
import net.xiaoyu233.fml.classloading.ModClassLoader;
import net.xiaoyu233.fml.config.Config;
import net.xiaoyu233.fml.config.JsonConfig;
import net.xiaoyu233.fml.relaunch.Launch;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] var0) {
        FishModLoader.setIsServer(false);
        Launch.launch("net.minecraft.client.main.Main",var0);
    }
}
