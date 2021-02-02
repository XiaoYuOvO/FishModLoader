package net.xiaoyu233.fml.asm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.xiaoyu233.fml.util.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Mapping {
    private static final BiMap<String, String> classMapping = HashBiMap.create();
    private static final BiMap<String, String> methodMapping = HashBiMap.create();
    private static final BiMap<String, String> fieldMapping = HashBiMap.create();
    private static final Function<String, String> classRule = (s) -> {
        String str = s;
        if (!s.contains(".")) {
            str = "net.minecraft." + s;
        }

        return str;
    };
    private static final Function<String, String> methodRule = (s) -> s;
    private static final Function<String, String> fieldRule = (s) -> s;

    public static void loadMappingFromJar(){
        for (MappingType value : MappingType.values()) {
            InputStream stream = Mapping.class.getResourceAsStream("/"+value.name().toLowerCase() + ".mapping");
            addMappingFromStream(stream,value);
        }
    }

    public static void loadMappingFromDir(String mappingDir) throws FileNotFoundException {
        for (MappingType value : MappingType.values()) {
            File file = new File(mappingDir, value.name().toLowerCase() + ".mapping");
            addMappingFromFile(file,value);
        }
    }

    public static Map<String, String> addMappingFromFile(File mappingFile,MappingType type) throws
            FileNotFoundException {
        if (!mappingFile.exists()) {
            try {
                mappingFile.getParentFile().mkdirs();
                mappingFile.createNewFile();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }
        return addMappingFromStream(new FileInputStream(mappingFile),type);
    }


    public static Map<String ,String> addMappingFromStream(InputStream stream,MappingType type){
        HashMap<String, String> map = new HashMap<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] entry = line.split(":");
                type.inMap.put(entry[0].trim(), entry[1].trim());
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return map;
    }
    public static String getClassMapName(String original) {
        return classMapping.getOrDefault(original, addClassName(original));
    }

    public static String getClassOriginalName(String mapName) {
        return classMapping.inverse().getOrDefault(mapName, mapName);
    }

    public static String getMethodMapName(String original) {
        return methodMapping.getOrDefault(original, addMethodName(original)).substring(original.indexOf(".") + 1);
    }

    public static String getMethodMapName(String original,String desc) {
        String name = methodMapping.getOrDefault(original + desc, addMethodName(original));
        if (name.contains("(")) {
            String n = name.substring(name.indexOf(".") + 1);
            return n.substring(0,n.indexOf("("));
        }
        return name.substring(name.indexOf(".") + 1);
    }


    public static String getMethodOriginalName(String mapName) {
        return methodMapping.inverse().get(mapName);
    }

    public static String getFieldMapName(String original) {
        return fieldMapping.getOrDefault(original, addFieldName(original)).substring(original.indexOf(".") + 1);
    }

    public static String addClassName(String name) {
        if (!name.equals("Z") && !name.equals("C") && !name.equals("B") && !name.equals("S") && !name.equals("I") && !name.equals("F") && !name.equals("J") && !name.equals("D")) {
            if (classMapping.containsValue(name)) {
                return name;
            } else {
                if (!classMapping.containsKey(name)) {
                    classMapping.put(name, classRule.apply(name));
                }

                return classMapping.get(name);
            }
        } else {
            return name;
        }
    }

    public static String addMethodName(String name) {
        methodMapping.put(name, name.substring(0, name.indexOf(".") + 1) + methodRule.apply(name.substring(name.indexOf(".") + 1)));
        return methodMapping.get(name).substring(name.indexOf(".") + 1);
    }

    public static String getOriginalName(String mapName) {
        return classMapping.inverse().getOrDefault(mapName, mapName);
    }

    public static String addFieldName(String name) {
        if (Utils.isJavaType(name)) {
            return name;
        } else {
            fieldMapping.put(name, name.substring(0, name.indexOf(".") + 1) + fieldRule.apply(name.substring(name.indexOf(".") + 1)));
            return fieldMapping.get(name).substring(name.indexOf(".") + 1);
        }
    }

    public enum MappingType{
        CLASS(classMapping),
        METHOD(methodMapping),
        FIELD(fieldMapping);
        private final BiMap<String,String> inMap;
        MappingType(BiMap<String,String> inMap){
            this.inMap = inMap;
        }
    }
}
