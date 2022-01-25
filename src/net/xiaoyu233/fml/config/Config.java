package net.xiaoyu233.fml.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.istack.internal.NotNull;
import net.xiaoyu233.fml.FishModLoader;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Config {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String name;
    protected Config(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public abstract ReadResult read(JsonElement json);

    public void readFromFile(File cfgFile){
        Config.ReadResult read = Config.ReadResult.NO_CHANGE;
        ConfigRegistry configRegistry = new ConfigRegistry(this, cfgFile);
        FishModLoader.addConfigRegistry(configRegistry);
        File configFile = configRegistry.getPathToConfigFile();
        if (!configFile.exists()){
            try {
                configFile.getParentFile().mkdirs();
                if (!configFile.createNewFile()) {
                    FishModLoader.LOGGER.error("Cannot create config file");
                }else {
                    try (FileWriter writer = new FileWriter(configFile)){
                        GSON.toJson(this.writeDefault(),writer);
                    }
                }
            } catch (IOException e) {
                FishModLoader.LOGGER.error("Cannot create config file",e);
            }
        }
        try (FileReader reader = new FileReader(configFile)){
            read= this.read(new JsonParser().parse(reader));
        }catch (Throwable e) {
            FishModLoader.LOGGER.error("Error in reading config",e);
        }
        try {
            if (read.isDirty()){
                try (FileWriter writer = new FileWriter(configFile)){
                    GSON.toJson(read.getChanged(),writer);
                }
            }
        }catch (Throwable e) {
            FishModLoader.LOGGER.error("Error in writing config",e);
        }
    }

    public abstract JsonElement writeDefault();

    public abstract JsonElement write();

    public static class ReadResult{
        public static final ReadResult NO_CHANGE = new ReadResult(false,null);
        @Nullable
        private final JsonElement changed;
        private final boolean dirty;

        private ReadResult(boolean dirty, @Nullable JsonElement changed) {
            this.dirty = dirty;
            this.changed = changed;
        }

        public static ReadResult ofChanged(JsonElement changed){
            return new ReadResult(true,changed);
        }

        @Nullable
        public JsonElement getChanged() {
            return changed;
        }

        public boolean isDirty() {
            return dirty;
        }
    }
}
