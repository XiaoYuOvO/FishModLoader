package net.xiaoyu233.fml.relaunch.client;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.relaunch.Launch;

import java.io.IOException;

public class Main {
    public static void main(String[] var0) throws IOException {
        System.setProperty("file.encoding","UTF-8");
        FishModLoader.setIsServer(false);
        FishModLoader.extractOpenAL();
        Launch.launch("net.minecraft.client.main.Main",var0);
    }
}
