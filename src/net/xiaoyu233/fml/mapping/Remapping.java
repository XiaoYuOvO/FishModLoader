package net.xiaoyu233.fml.mapping;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.md_5.specialsource.CustomRemapper;
import net.xiaoyu233.fml.util.Utils;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.extensibility.IRemapper;
import org.spongepowered.asm.util.ObfuscationUtil;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Remapping extends CustomRemapper implements IRemapper, ObfuscationUtil.IClassRemapper {
   public final Map<String, List<String>> interfaceMap = new HashMap<>();
   public final Map<String, String> superClassMap = new HashMap<>();
   public final BiMap<String, String> classMapping = HashBiMap.create();
   public final Map<String, String> methodMapping = new HashMap<>();
   public final Map<String, String> fieldMapping = new HashMap<>();
   private static final Function<String, String> classRule = (s) -> {
      String str = s;
      if (!s.contains(".")) {
         str = "net/minecraft/" + s;
      }

      return str;
   };
   private static final Function<String, String> methodRule = (s) -> {
      return s;
   };
   private static final Function<String, String> fieldRule = (s) -> {
      return s;
   };
   private BiMap<String, String> inverse;

   public void loadMappingFromJar() {
      Remapping.MappingType[] var1 = Remapping.MappingType.values();
      int var2 = var1.length;

      for (MappingType value : var1) {
         InputStream stream = Remapping.class.getResourceAsStream("/" + value.name().toLowerCase() + ".mapping");
         this.addMappingFromStream(stream, value);
      }

   }

   public void loadMappingFromDir(String mappingDir) throws FileNotFoundException {
      Remapping.MappingType[] var2 = Remapping.MappingType.values();
      int var3 = var2.length;

      for (MappingType value : var2) {
         File file = new File(mappingDir, value.name().toLowerCase() + ".mapping");
         this.addMappingFromFile(file, value);
      }

   }

   public Map<String, String> addMappingFromFile(File mappingFile, Remapping.MappingType type) throws FileNotFoundException {
      if (!mappingFile.exists()) {
         try {
            mappingFile.getParentFile().mkdirs();
            mappingFile.createNewFile();
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

      return this.addMappingFromStream(new FileInputStream(mappingFile), type);
   }

   public boolean isClassMapped(String newName) {
      return this.classMapping.containsValue(newName);
   }

   public Map<String, String> addMappingFromStream(InputStream stream, Remapping.MappingType type) {
      HashMap<String, String> map = new HashMap<>();
      Map<String, String> inMap;
      switch(type) {
      case CLASS:
         inMap = this.classMapping;
         inverse = this.classMapping.inverse();
         break;
      case METHOD:
         inMap = this.methodMapping;
         break;
      case FIELD:
         inMap = this.fieldMapping;
         break;
      default:
         throw new IllegalStateException("Unexpected value: " + type);
      }

      try {
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

         String line;
         while((line = bufferedReader.readLine()) != null) {
            String[] entry = line.split(":");
            (inMap).put(entry[0].trim(), entry[1].trim());
         }
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      return map;
   }

   public String getClassMapName(String original) {
      String s = this.classMapping.get(original);
      if (s != null) {
         return s;
      } else if (!original.contains("/") && !original.contains(".")) {
         String newName = "net.minecraft." + original;
         this.classMapping.put(original, newName);
         return newName;
      } else {
         return original;
      }
   }

   public String getMethodMapName(String original) {
      return this.methodMapping.getOrDefault(original, this.addMethodName(original)).substring(original.indexOf(".") + 1);
   }

   public String getMethodMapName(String original, String desc) {
      String name = this.methodMapping.get(original + desc);
      if (name == null) {
         return null;
      } else if (name.contains("(")) {
         String n = name.substring(name.indexOf(".") + 1);
         return n.substring(0, n.indexOf("("));
      } else {
         return name.substring(name.indexOf(".") + 1);
      }
   }

   public String getMethodMapNameRemapSig(String original, String desc) {
      String name = this.methodMapping.get(original + desc);
      if (name == null) {
         return null;
      } else if (name.contains("(")) {
         String n = name.substring(name.indexOf(".") + 1);
         return n.substring(0, n.indexOf("("));
      } else {
         return name.substring(name.indexOf(".") + 1);
      }
   }

   public String getFieldMapName(String original) {
      String newName = this.fieldMapping.get(original);
      return newName != null ? newName.substring(newName.indexOf(".") + 1) : null;
   }

   public String addClassName(String name) {
      if (!name.equals("Z") && !name.equals("C") && !name.equals("B") && !name.equals("S") && !name.equals("I") && !name.equals("F") && !name.equals("J") && !name.equals("D")) {
         if (this.classMapping.containsValue(name)) {
            return name;
         } else {
            if (!this.classMapping.containsKey(name)) {
               this.classMapping.put(name, classRule.apply(name));
            }

            return this.classMapping.get(name);
         }
      } else {
         return name;
      }
   }

   public String addMethodName(String name) {
      this.methodMapping.put(name, name.substring(0, name.indexOf(".") + 1) + methodRule.apply(name.substring(name.indexOf(".") + 1)));
      return this.methodMapping.get(name).substring(name.indexOf(".") + 1);
   }

   public String getOriginalName(String mapName) {
      return this.classMapping.inverse().getOrDefault(mapName, mapName);
   }

   public String addFieldName(String name) {
      if (Utils.isJavaType(name)) {
         return name;
      } else {
         this.fieldMapping.put(name, name.substring(0, name.indexOf(".") + 1) + fieldRule.apply(name.substring(name.indexOf(".") + 1)));
         return this.fieldMapping.get(name).substring(name.indexOf(".") + 1);
      }
   }

   public void addInterfaceMap(String className, List<String> interfaceNames) {
      this.interfaceMap.put(className, interfaceNames);
   }

   private String remapMethodDesc(String var1) {
      String var2 = var1.substring(var1.indexOf(41) + 1);
      var2 = this.remapFieldDesc(var2);
      Type[] var3 = Type.getArgumentTypes(var1);
      StringBuilder var4 = new StringBuilder();

      for (Type var8 : var3) {
         var4.append(this.remapFieldDesc(var8.getDescriptor()));
      }

      return "(" + var4 + ")" + var2;
   }

   public String replaceFieldDesc(String var1) {
      if (Utils.isJavaType(var1)) {
         return var1;
      } else {
         int var2 = 0;

         while(true) {
            char var3 = var1.charAt(var2);
            if (var3 != '[' && var3 != 'L') {
               String var4 = var1.substring(var2, var1.length() - 1);
               String var5 = this.getClassMapName(var4.replace("/", "."));
               return var1.substring(0, var2) + var5.replace(".", "/") + ";";
            }

            ++var2;
         }
      }
   }

   private String remapFieldDesc(String var1) {
      if (Utils.isJavaType(var1)) {
         return var1;
      } else {
         int var2 = 0;

         while(true) {
            char var3 = var1.charAt(var2);
            if (var3 != '[' && var3 != 'L') {
               String var4 = var1.substring(var2, var1.length() - 1);
               String var5 = this.getOriginalName(var4.replace("/", "."));
               return var1.substring(0, var2) + var5.replace(".", "/") + ";";
            }

            ++var2;
         }
      }
   }

   public String replaceMethodDesc(String var1) {
      String var2 = var1.substring(var1.indexOf(41) + 1);
      var2 = this.replaceFieldDesc(var2);
      Type[] var3 = Type.getArgumentTypes(var1);
      StringBuilder var4 = new StringBuilder();

      for (Type var8 : var3) {
         var4.append(this.replaceFieldDesc(var8.getDescriptor()));
      }

      return "(" + var4 + ")" + var2;
   }

   public void addSuperclassMapping(String subClass, String superClass) {
      this.superClassMap.put(subClass, superClass);
   }

   public String mapMethodName(String owner, String name, String desc, int access) {
      String methodMapName = this.getMethodMapName(owner + "." + name, desc);
      if (methodMapName == null) {
         for(String superName = this.superClassMap.get(owner); superName != null && !superName.equals("java/lang/Object"); superName = this.superClassMap.get(superName)) {
            methodMapName = this.getMethodMapName(superName + "." + name, desc);
            if (methodMapName != null) {
               break;
            }
         }

         if (methodMapName == null) {
            List<String> interfaceNames = this.interfaceMap.get(owner);
            if (interfaceNames != null) {

               for (String interfaceName : interfaceNames) {
                  methodMapName = this.getMethodMapName(interfaceName + "." + name, desc);
                  if (methodMapName != null) {
                     break;
                  }
               }
            }
         }
      }

      return methodMapName == null ? name : methodMapName;
   }

   public String mapFieldName(String owner, String name, String desc, int access) {
      String fieldMapName = this.getFieldMapName(owner + "." + name + desc);
      if (fieldMapName == null) {
         for(String superName = this.superClassMap.get(owner); superName != null && !superName.equals("java/lang/Object"); superName = this.superClassMap.get(superName)) {
            fieldMapName = this.getFieldMapName(superName + "." + name + desc);
            if (fieldMapName != null) {
               break;
            }
         }
      }

      return fieldMapName == null ? name : fieldMapName;
   }

   public void addFieldName(String owner, String name, String desc, int access, String newName) {
      this.fieldMapping.put(owner + "." + name, newName);
   }

   public String map(String typeName) {
      return this.getClassMapName(typeName).replace(".", "/");
   }

   public String unmap(String typeName) {
      return inverse.get(typeName);
   }

   public String mapDesc(String desc) {
      return super.mapDesc(desc);
   }

   public String unmapDesc(String desc) {
      return ObfuscationUtil.unmapDescriptor(desc,this);
   }

   public enum MappingType {
      CLASS,
      METHOD,
      FIELD
   }
}
