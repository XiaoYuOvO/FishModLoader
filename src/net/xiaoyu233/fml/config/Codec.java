package net.xiaoyu233.fml.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class Codec<T> {
    private static final Map<Class<?>,Codec<?>> types = new HashMap<>();
    public static final Codec<Boolean> BOOLEAN = new Codec<Boolean>(Boolean.class) {
        @Override
        public Boolean read(JsonElement json) {
            return json.getAsBoolean();
        }

        @Override
        public JsonElement write(Boolean value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<Double> DOUBLE = new Codec<Double>(Double.class) {
        @Override
        public Double read(JsonElement json) {
            return json.getAsDouble();
        }

        @Override
        public JsonElement write(Double value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<Float> FLOAT = new Codec<Float>(Float.class) {
        @Override
        public Float read(JsonElement json) {
            return json.getAsFloat();
        }

        @Override
        public JsonElement write(Float value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<File> FILE = new Codec<File>(File.class) {
        @Override
        public File read(JsonElement json) {
            return new File(json.getAsString());
        }

        @Override
        public JsonElement write(File value) {
            return new JsonPrimitive(value.toString());
        }
    };
    public static final Codec<Integer> INTEGER = new Codec<Integer>(Integer.class) {
        @Override
        public Integer read(JsonElement json) {
            return json.getAsInt();
        }

        @Override
        public JsonElement write(Integer value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<String> STRING = new Codec<String>(String.class) {
        @Override
        public String read(JsonElement json) {
            return json.getAsString();
        }

        @Override
        public JsonElement write(String value) {
            return new JsonPrimitive(value);
        }
    };
    private final Class<T> typeClass;
    private Codec(Class<T> typeClass){
        types.put(typeClass,this);
        this.typeClass = typeClass;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public static <T> Codec<T> getFromClass(Class<T> clazz){
        return (Codec<T>) types.get(clazz);
    }

    public abstract T read(JsonElement json);

    public abstract JsonElement write(T value);
}
