package net.xiaoyu233.fml.config;

import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JsonConfig implements Config {
    private final File configFile;
    private Map<String, Object> configMap = new HashMap<>();

    public JsonConfig(File configFile) {
        this.configFile = configFile;
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }

    }

    public <T> T get(String string) {
        return (T) this.configMap.get(string);
    }

    public void set(String string, Object obj) {
        this.configMap.put(string, obj);
    }

    public void save() {
        String jsonString = new Gson().toJson(this.configMap);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(this.configFile);
            Throwable var3 = null;

            try {
                fileOutputStream.write(jsonString.getBytes());
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if (fileOutputStream != null) {
                    if (var3 != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        fileOutputStream.close();
                    }
                }

            }
        } catch (FileNotFoundException var16) {
            var16.printStackTrace();
        } catch (IOException var17) {
            var17.printStackTrace();
        }

    }

    public void load() {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.configFile);
            Throwable var2 = null;

            try {
                Gson gson = new Gson();
                this.configMap = gson.fromJson(new FileReader(this.configFile), Map.class);
                if (this.configMap == null) {
                    this.configMap = new HashMap<>();
                }
            } catch (Throwable var13) {
                var2 = var13;
                throw var13;
            } finally {
                if (fileInputStream != null) {
                    if (var2 != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable var12) {
                            var2.addSuppressed(var12);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }

            }
        } catch (FileNotFoundException var15) {
            var15.printStackTrace();
        } catch (IOException var16) {
            var16.printStackTrace();
        }

    }
}