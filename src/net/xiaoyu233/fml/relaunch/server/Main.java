package net.xiaoyu233.fml.relaunch.server;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.relaunch.Launch;

import java.io.IOException;

public class Main {
    public static void main(String[] var0) throws IOException {
        FishModLoader.setIsServer(true);
        Launch.launch("net.minecraft.server.MinecraftServer",var0);
    }
}
