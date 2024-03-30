package net.xiaoyu233.fml.config;

import com.google.gson.JsonElement;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.FieldReference;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RangedConfigEntry<T extends Comparable<T>> extends ConfigEntry<T> {
    private final Range<T> valueRange;
    RangedConfigEntry(String name, Codec<T> codec, T defaultValue, FieldReference<T> configRef, Range<T> valueRange) {
        super(name, codec, defaultValue, configRef);
        this.valueRange = valueRange;
        if (!valueRange.contains(defaultValue)) throw new IllegalArgumentException("Default value " + defaultValue + "is out of range " + valueRange + " for config entry " + name);
    }

    RangedConfigEntry(String name, FieldReference<T> configRef, Range<T> valueRange) {
        //noinspection unchecked
        this(name, (Codec<T>) Codec.getFromClass(configRef.getValueClass()), configRef.get(), configRef, valueRange);
    }

    RangedConfigEntry(String name, Codec<T> codec, FieldReference<T> configRef, Range<T> valueRange) {
        this(name, codec, configRef.get(), configRef, valueRange);
    }

    @Override
    public void setCurrentValue(T value) {
        super.setCurrentValue(fit(value));
    }

    @Nonnull
    @Override
    public ReadResult read(JsonElement json) {
        try {
            if (json != null){
                if (json.isJsonObject()){
                    T read = this.codec.read(json.getAsJsonObject().get("value"));
                    T clamped = fit(read);
                    this.configRef.set(clamped);
                    if (!json.getAsJsonObject().get("_comment").getAsString().equals(this.comment) || read != clamped) {
                        return Config.ReadResult.ofChanged(this.writeWithValue(this.configRef.get()));
                    }
                }else {
                    T read = this.codec.read(json);
                    T clamped = fit(read);
                    this.configRef.set(clamped);
                    if (this.comment != null && !this.comment.isEmpty() || read != clamped){
                        return Config.ReadResult.ofChanged(this.writeWithValue(this.configRef.get()));
                    }
                }
                return Config.ReadResult.NO_CHANGE;
            }else {
                this.configRef.set(this.defaultValue);
                return Config.ReadResult.ofChanged(this.writeDefault());
            }
        }catch (Throwable t) {
            FishModLoader.LOGGER.error("Cannot read config: " + this.getName(),t);
            this.configRef.set(this.defaultValue);
            return Config.ReadResult.ofChanged(this.writeDefault());
        }
    }

    @Override
    public ConfigEntry<T> withComment(String comment) {
        return super.withComment(comment + "∈" + this.valueRange.toString());
    }

    private T fit(T element) {
        Objects.requireNonNull(element, "element");
        if (valueRange.isAfter(element)) {
            return valueRange.getMinimum();
        } else {
            return valueRange.isBefore(element) ? valueRange.getMaximum() : element;
        }
    }
}
