package net.xiaoyu233.fml.relaunch.server;

import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.xiaoyu233.fml.relaunch.LaunchDelegate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
   public static void main(String[] var0) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, ModResolutionException {
      System.setProperty("file.encoding", "UTF-8");
      LaunchDelegate.delegateLaunch("net.minecraft.server.MinecraftServer", var0, true);
   }
}
