package net.xiaoyu233.fml.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.File;

public interface Codec<T> {
    Codec<Boolean> BOOLEAN = new Codec<Boolean>() {
        @Override
        public Boolean read(JsonElement json) {
            return json.getAsBoolean();
        }

        @Override
        public JsonElement write(Boolean value) {
            return new JsonPrimitive(value);
        }
    };
    Codec<Double> DOUBLE = new Codec<Double>() {
        @Override
        public Double read(JsonElement json) {
            return json.getAsDouble();
        }

        @Override
        public JsonElement write(Double value) {
            return new JsonPrimitive(value);
        }
    };
    Codec<File> FILE = new Codec<File>() {
        @Override
        public File read(JsonElement json) {
            return new File(json.getAsString());
        }

        @Override
        public JsonElement write(File value) {
            return new JsonPrimitive(value.toString());
        }
    };
    Codec<Integer> INTEGER = new Codec<Integer>() {
        @Override
        public Integer read(JsonElement json) {
            return json.getAsInt();
        }

        @Override
        public JsonElement write(Integer value) {
            return new JsonPrimitive(value);
        }
    };
    Codec<String> STRING = new Codec<String>() {
        @Override
        public String read(JsonElement json) {
            return json.getAsString();
        }

        @Override
        public JsonElement write(String value) {
            return new JsonPrimitive(value);
        }
    };

    T read(JsonElement json);

    JsonElement write(T value);
}
