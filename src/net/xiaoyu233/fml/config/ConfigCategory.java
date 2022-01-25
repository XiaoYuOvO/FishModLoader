package net.xiaoyu233.fml.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.istack.internal.NotNull;
import net.xiaoyu233.fml.FishModLoader;

import java.util.ArrayList;
import java.util.List;

public class ConfigCategory extends Config {
    private final List<Config> child = new ArrayList<>();
    public ConfigCategory(String name) {
        super(name);
    }

    public static ConfigCategory of(String name){
        return new ConfigCategory(name);
    }

    public ConfigCategory addEntry(Config entry){
        this.child.add(entry);
        return this;
    }

    public List<Config> getChild() {
        return child;
    }

    @Override
    @NotNull
    public ReadResult read(JsonElement json) {
        try {
            if (json.isJsonObject()){
                JsonObject obj = ((JsonObject) json);
                boolean oneChanged = false;
                for (Config config : this.child) {
                    String name = config.getName();
                    ReadResult result = config.read(obj.get(name));
                    if (result.isDirty()){
                        oneChanged = true;
                        if (obj.has(name)){
                            obj.remove(name);
                        }
                        obj.add(name,result.getChanged());
                    }
                }
                if (oneChanged){
                    return ReadResult.ofChanged(obj);
                }
            }
            return ReadResult.NO_CHANGE;
        }catch (Throwable t) {
            FishModLoader.LOGGER.error("Cannot read config: " + this.getName(),t);
            return ReadResult.ofChanged(this.writeDefault());
        }
    }

    @Override
    public JsonElement writeDefault() {
        JsonObject result = new JsonObject();
        for (Config config : child) {
            result.add(config.getName(),config.writeDefault());
        }
        return result;
    }

    @Override
    public JsonElement write() {
        JsonObject result = new JsonObject();
        for (Config config : child) {
            result.add(config.getName(),config.write());
        }
        return result;
    }
}
