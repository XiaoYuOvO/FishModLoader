package net.xiaoyu233.fml.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.xiaoyu233.fml.FishModLoader;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfigCategory extends Config {
    protected String comment = null;
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
    public ConfigCategory withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getComment() {
        return comment;
    }

    @Nonnull
    @Override
    public ReadResult read(JsonElement json) {
        try {
            if (json.isJsonObject()){
                JsonObject obj = ((JsonObject) json);
                boolean oneChanged = false;
                if (this.comment != null && !this.comment.isEmpty()){
                    boolean hasComment = obj.has("_comment");
                    if (hasComment && !obj.get("_comment").getAsString().equals(this.comment)){
                        obj.remove("_comment");
                        obj.addProperty("_comment", this.comment);
                        oneChanged = true;
                    }else if (!hasComment){
                        obj.addProperty("_comment", this.comment);
                        oneChanged = true;
                    }
                }
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
    public JsonObject writeDefault() {
        JsonObject result = new JsonObject();
        for (Config config : child) {
            result.add(config.getName(),config.writeDefault());
        }
        if (this.comment != null && !this.comment.isEmpty()){
            result.addProperty("_comment", this.getComment());
        }
        return result;
    }

    @Override
    public JsonObject write() {
        JsonObject result = new JsonObject();
        for (Config config : child) {
            result.add(config.getName(),config.write());
        }
        if (this.comment != null && !this.comment.isEmpty()){
            result.addProperty("_comment", this.getComment());
        }
        return result;
    }
}
