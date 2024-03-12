package net.xiaoyu233.fml.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;

import java.util.ArrayList;
import java.util.List;

public class RemoteModInfo {
    private final ModEnvironment dists;
    private final Version modVer;
    private final String modid;

    public RemoteModInfo(LoaderModMetadata mod) {
        this.modid = mod.getId();
        this.modVer = mod.getVersion();
        this.dists = mod.getEnvironment();
    }

    public RemoteModInfo(ModEnvironment dists, Version modVer, String modid) {
        this.dists = dists;
        this.modVer = modVer;
        this.modid = modid;
    }

    public static List<RemoteModInfo> readFromJson(JsonArray array) throws VersionParsingException {
        ArrayList<RemoteModInfo> objects = new ArrayList<>();
        for (JsonElement jsonElement : array) {
            if (jsonElement.isJsonObject()){
                JsonObject object = jsonElement.getAsJsonObject();
                objects.add(new RemoteModInfo(ModEnvironment.valueOf(object.get("env").getAsString()), Version.parse(object.get("version").getAsString()), object.get("modId").getAsString()));
            }
        }
        return objects;
    }

    public static JsonElement writeToJson(List<RemoteModInfo> infos){
        JsonArray array = new JsonArray();
        for (RemoteModInfo info : infos) {
            JsonObject object = new JsonObject();
            object.addProperty("modId", info.getModid());
            object.addProperty("version", info.getModVer().toString());
            object.addProperty("env", info.dists.name());
            array.add(object);
        }
        return array;
    }

    public boolean canBeUsedAt(EnvType dist) {
        return this.dists.matches(dist);
    }

    public Version getModVer() {
        return this.modVer;
    }

    public String getModid() {
        return this.modid;
    }
}
