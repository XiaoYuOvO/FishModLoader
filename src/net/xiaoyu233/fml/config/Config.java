package net.xiaoyu233.fml.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Config {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String name;
    protected Config(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    public abstract ReadResult read(JsonElement json);

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
