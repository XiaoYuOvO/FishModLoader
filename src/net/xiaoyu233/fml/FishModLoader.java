package net.xiaoyu233.fml;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.xiaoyu233.fml.asm.annotations.Dist;
import net.xiaoyu233.fml.config.Config;
import net.xiaoyu233.fml.config.JsonConfig;
import net.xiaoyu233.fml.util.ModInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FishModLoader {
    private static final ArrayList<ModInfo> mods = new ArrayList<>();
    private static final Map<String,ModInfo> modsMap = new HashMap<>();
    private static final Map<String,ModInfo> modsMapForLoginCheck;
    private static final boolean allowsClientMods;
    private static final boolean sideSett=false;
    private static boolean isServer = false;
    public static JsonConfig config;
    public static void addModInfo(ModInfo modInfo){
        FishModLoader.mods.add(modInfo);
        modsMap.put(modInfo.getModid(),modInfo);
        if (modInfo.canBeUsedAt(Dist.CLIENT)){
            modsMapForLoginCheck.put(modInfo.getModid(),modInfo);
        }
    }
    public static ArrayList<ModInfo> getMods(){
        return ((ArrayList<ModInfo>) mods.clone());
    }

    public static JsonElement getModsJson(){
        return new Gson().toJsonTree(mods);
    }

    public static void setIsServer(boolean isServer){
        if (!sideSett){
            FishModLoader.isServer = isServer;
        }
    }

    public static boolean isServer() {
        return isServer;
    }

    private static void saveDefault(Config var0) {
        var0.set("jarPath", "server.jar");
        var0.set("debug", Boolean.FALSE);
        var0.save();
    }


    public static Map<String, ModInfo> getModsMapForLoginCheck() {
        return new HashMap<>(modsMapForLoginCheck);
    }

    public static Map<String, ModInfo> getModsMap() {
        return new HashMap<>(modsMap);
    }

    public static boolean isAllowsClientMods() {
        return allowsClientMods;
    }
    static {
        File config = new File(System.getProperty("user.dir"));
        FishModLoader.config = new JsonConfig(new File(config, "config.json"));
        FishModLoader.config.load();
        String var3 = FishModLoader.config.get("jarPath");
        if (var3 == null || var3.isEmpty()) {
            saveDefault(FishModLoader.config);
            FishModLoader.config.load();
        }
        if (isServer()) {
            allowsClientMods = FishModLoader.config.get("allowsClientMods");
        }else {
            allowsClientMods = true;
        }
        modsMapForLoginCheck = new HashMap<>();
        addModInfo(new ModInfo("FishModLoader","0.1.0B",1,Dist.SERVER,Dist.CLIENT));
    }


}
