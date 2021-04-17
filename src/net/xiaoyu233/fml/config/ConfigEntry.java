package net.xiaoyu233.fml.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.FieldReference;

public class ConfigEntry<T> extends Config {
    private final Codec<T> codec;
    private final FieldReference<T> configRef;
    private final T defaultValue;
    protected String comment = null;
    public ConfigEntry(String name, Codec<T> codec, T defaultValue, FieldReference<T> configRef) {
        super(name);
        this.codec = codec;
        this.defaultValue = defaultValue;
        this.configRef = configRef;
    }
    public ConfigEntry(String name,FieldReference<T> configRef) {
        super(name);
        this.codec = (Codec<T>) Codec.getFromClass(configRef.getValueClass());
        this.defaultValue = configRef.get();
        this.configRef = configRef;
    }
    public ConfigEntry(String name, Codec<T> codec,FieldReference<T> configRef) {
        super(name);
        this.codec = codec;
        this.defaultValue = configRef.get();
        this.configRef = configRef;
    }

    public static <T> ConfigEntry<T> of(String name,FieldReference<T> configRef){
        return new ConfigEntry<>(name, configRef);
    }


    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Config.ReadResult read(JsonElement json) {
        try {
            if (json.isJsonObject()){
                this.configRef.set(this.codec.read(json.getAsJsonObject().get("value")));
            }else {
                this.configRef.set(this.codec.read(json));
            }
            return Config.ReadResult.NO_CHANGE;
        }catch (Throwable t) {
            FishModLoader.LOGGER.error("Cannot read config: " + this.getName(),t);
            this.configRef.set(this.defaultValue);
            return Config.ReadResult.ofChanged(this.writeDefault());
        }
    }

    public ConfigEntry<T> withComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public JsonElement writeDefault() {
        if (this.comment != null){
            JsonObject json = new JsonObject();
            json.addProperty("_comment",this.comment);
            json.add("value",this.codec.write(this.defaultValue));
            return json;
        }
        return this.codec.write(this.defaultValue);
    }
}
