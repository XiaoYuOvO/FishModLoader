package net.xiaoyu233.fml.config;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.xiaoyu233.fml.FishModLoader;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

public class ConfigRoot extends ConfigCategory{
    private static final String CONFIG_VERSION_NAME = "config_version";
    private final int configVersion;

    public ConfigRoot(int configVersion) {
        super("root");
        this.configVersion = configVersion;
    }

    public static ConfigRoot create(int configVersion){
        return new ConfigRoot(configVersion);
    }

    public ConfigRoot addEntry(Config entry){
        super.addEntry(entry);
        return this;
    }

    public ConfigRoot withComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public JsonObject writeDefault() {
        JsonObject object = super.writeDefault();
        object.addProperty(CONFIG_VERSION_NAME,configVersion);
        return object;
    }

    public void readFromFile(File cfgFile){
        Config.ReadResult read = Config.ReadResult.NO_CHANGE;
        File configFile = new File(FishModLoader.CONFIG_DIR,cfgFile.toString());
        if (!configFile.exists()){
            try {
                configFile.getParentFile().mkdirs();
                if (!configFile.createNewFile()) {
                    FishModLoader.LOGGER.error("Cannot create config file");
                }else {
                    try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), Charsets.UTF_8)){
                        JsonObject jsonElement = this.writeDefault();
                        jsonElement.addProperty(CONFIG_VERSION_NAME, configVersion);
                        GSON.toJson(jsonElement,writer);
                    }
                }
            } catch (IOException e) {
                FishModLoader.LOGGER.error("Cannot create config file",e);
            }
        }
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(configFile.toPath()), Charsets.UTF_8)){
            read = this.read(new JsonParser().parse(reader));
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

    @Nonnull
    @Override
    public ReadResult read(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject originalObj = json.getAsJsonObject();
            if (originalObj.has(CONFIG_VERSION_NAME)) {
                int inFileVersion = originalObj.get(CONFIG_VERSION_NAME).getAsInt();
                if (inFileVersion < this.configVersion) {
                    return ReadResult.ofChanged(writeDefault());
                }
            }else {
                JsonObject resultObj = originalObj;
                ReadResult read = super.read(json);
                if (read.isDirty()) {
                    resultObj = Objects.requireNonNull(read.getChanged()).getAsJsonObject();
                }
                resultObj.addProperty(CONFIG_VERSION_NAME,this.configVersion);
                return ReadResult.ofChanged(resultObj);
            }
        }
        return super.read(json);
    }
}
