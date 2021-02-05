package net.xiaoyu233.fml.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JsonConfig implements Config {
   private final File configFile;
   private Map<String, Object> configMap = new HashMap();

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

   public int getInt(String string) {
      Object obj = this.configMap.get(string);
      if (obj instanceof Number) {
         return ((Number)obj).intValue();
      } else {
         throw new ClassCastException();
      }
   }

   public boolean has(String string) {
      return this.configMap.containsKey(string);
   }

   public void set(String string, Object obj) {
      this.configMap.put(string, obj);
   }

   public void save() {
      String jsonString = (new GsonBuilder()).setPrettyPrinting().create().toJson(this.configMap);

      try {
         FileOutputStream fileOutputStream = new FileOutputStream(this.configFile);
         Throwable var3 = null;

         try {
            fileOutputStream.write(jsonString.getBytes());
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (fileOutputStream != null) {
               if (var3 != null) {
                  try {
                     fileOutputStream.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  fileOutputStream.close();
               }
            }

         }
      } catch (IOException var15) {
         var15.printStackTrace();
      }

   }

   public void load() {
      try {
         FileInputStream fileInputStream = new FileInputStream(this.configFile);
         Throwable var2 = null;

         try {
            Gson gson = new Gson();
            this.configMap = (Map)gson.fromJson(new FileReader(this.configFile), Map.class);
            if (this.configMap == null) {
               this.configMap = new HashMap();
            }
         } catch (Throwable var12) {
            var2 = var12;
            throw var12;
         } finally {
            if (var2 != null) {
               try {
                  fileInputStream.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               fileInputStream.close();
            }

         }
      } catch (IOException var14) {
         var14.printStackTrace();
      }

   }
}
