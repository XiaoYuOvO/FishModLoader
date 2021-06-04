package net.xiaoyu233.fml.util;

import net.xiaoyu233.fml.mapping.Remapping;
import net.xiaoyu233.fml.mapping.Renamer;
import net.xiaoyu233.fml.relaunch.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MappingGenerator {
    private static final File deobfClassOut = new File("E:/DeobfCls");

    public static void fixSuperclassMapping(Map<String,ClassData> classMap,DebugRemapping remapping){
        for (Map.Entry<String, ClassData> stringClassDataEntry : classMap.entrySet()) {
            ClassNode classNode =stringClassDataEntry.getValue().getClassNode();
            List<ClassNode> interfaces = new ArrayList<>();
            for (String anInterface : classNode.interfaces) {
                if (classMap.containsKey(anInterface)){
                    interfaces.add(classMap.get(anInterface).getClassNode());
                }
            }
            Map<String,String> remapped = new HashMap<>();
            String name = classNode.name;
            for (MethodNode method : classNode.methods) {
                String remappedMd = remapping.mapMethodName(name, method.name, method.desc);
                if (!method.name.equals(remappedMd)){
                    remapped.put(method.name + method.desc,remappedMd);
                }
            }
            if (classMap.containsKey(classNode.superName)) {
                ClassNode superClass = classMap.get(classNode.superName).getClassNode();
                for (MethodNode method : superClass.methods) {
                    for (Map.Entry<String, String> stringStringEntry : remapped.entrySet()) {
                        String raw = stringStringEntry.getKey();
                        if ((method.name + method.desc).equals(raw)) {
                            String key = superClass.name + "." + raw;
                            remapping.methodMapping.put(key, stringStringEntry.getValue());
                            remapping.addMdRemap(key,name, stringStringEntry.getValue());
                            System.out.println("Add superclass remap:" + key + " -> " + stringStringEntry.getValue() + " by subclass: "  + name + "(" + remapping.map(name) + ")");
                        }
                    }
                }
            }
            for (Map.Entry<String, String> stringStringEntry : remapped.entrySet()) {
                String raw = stringStringEntry.getKey();
                for (ClassNode interfaceNode : interfaces) {
                    for (MethodNode method : interfaceNode.methods) {
                        if ((method.name + method.desc).equals(raw)){
                            String key = interfaceNode.name + "." + raw;
                            remapping.methodMapping.put(key, stringStringEntry.getValue());
                            remapping.addMdRemap(key,name, stringStringEntry.getValue());
                            System.out.println("Add interface remap:" + key + " -> " + stringStringEntry.getValue() + " by subclass: "  + name + "(" + remapping.map(name) + ")");
                        }
                    }
                }
            }


        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            deobfClassOut.mkdirs();
            String path = args[0];
            DebugRemapping remapping = new DebugRemapping();
            remapping.addMappingFromStream(Launch.class.getResourceAsStream("/class.mapping"), Remapping.MappingType.CLASS);
            remapping.addMappingFromStream(Launch.class.getResourceAsStream("/method_backup.mapping"), Remapping.MappingType.METHOD);
            remapping.addMappingFromStream(Launch.class.getResourceAsStream("/field_backup.mapping"), Remapping.MappingType.FIELD);
            Renamer renamer = new Renamer(remapping);
            if (path.endsWith(".jar")) {
                JarFile minecraftJar = new JarFile(path);
                Enumeration<JarEntry> entries = minecraftJar.entries();
                Map<String,ClassData> classMap = new HashMap<>();
                while(entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        InputStream inputStream = minecraftJar.getInputStream(entry);
                        byte[] basicClass = Utils.readAllBytes(inputStream);
                        ClassReader reader = new ClassReader(basicClass);
                        ClassNode classNode = new ClassNode();
                        reader.accept(classNode, 0);
                        String mapped = remapping.map(classNode.name);
                        if (!mapped.contains("net/minecraft")){
                            continue;
                        }
                        remapping.addSuperclassMapping(classNode.name, classNode.superName);
                        remapping.addInterfaceMap(classNode.name, classNode.interfaces);
                        classMap.put(classNode.name,new ClassData(classNode,basicClass));
                    }
                }
                minecraftJar.close();

                fixSuperclassMapping(classMap,remapping);
                remapping.setLogRemap(true);
                for (Map.Entry<String, ClassData> stringClassNodeEntry : classMap.entrySet()) {
                    ClassData value = stringClassNodeEntry.getValue();
                    ClassNode classNode = value.getClassNode();
                    String mapped = remapping.map(classNode.name);
                    File file;
                    if (mapped.contains("/")) {
                        File dir = new File(deobfClassOut, mapped.substring(0, mapped.lastIndexOf("/")));
                        file = new File(deobfClassOut, mapped + ".class");
                        dir.mkdirs();
                    } else {
                        file = new File(deobfClassOut, mapped + ".class");
                    }
                    file.createNewFile();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        System.out.println("Renaming: " + classNode.name + " -> " + mapped);
                        fileOutputStream.write(renamer.transform(classNode.name, mapped, value.getBytecode()));
                    }
                }
//                while (entries1.hasMoreElements()) {
//                    JarEntry entry = entries1.nextElement();
//                    String name = entry.getName();
//                    if (name.endsWith(".class")) {
//                        byte[] basicClass = Utils.readAllBytes(minecraftJar.getInputStream(entry));
//                        ClassReader reader = new ClassReader(basicClass);
//                        ClassNode classNode = new ClassNode();
//                        reader.accept(classNode, 0);
//
//                    }
//                }
                remapping.writeMappings();
            }
        }
    }

    static class ClassData{
        private final byte[] bytecode;
        private final ClassNode classNode;

        ClassData(ClassNode classNode, byte[] bytecode) {
            this.classNode = classNode;
            this.bytecode = bytecode;
        }

        public byte[] getBytecode() {
            return bytecode;
        }

        public ClassNode getClassNode() {
            return classNode;
        }
    }

//    static class MethodInfo{
//        private final String
//    }

    static class DebugRemapping extends Remapping{
        private final File clOutFile = new File("./class_.mapping");
        private final FileWriter clWriter = new FileWriter(clOutFile,false);
        private final Map<String,String> fdMap = new HashMap<>();
        private final File fdOutFile = new File("./field_.mapping");
        private final FileWriter fdWriter = new FileWriter(fdOutFile,false);
        private final Map<String,String> mdMap = new HashMap<>();
        private final File mdOutFile = new File("./method_.mapping");
        private final FileWriter mdWriter = new FileWriter(mdOutFile,false);
        private boolean logRemap;

        public DebugRemapping() throws IOException {
            super();
            this.createIfAbsent(mdOutFile);
            this.createIfAbsent(fdOutFile);
        }

        public void addFdRemap(String src,String owner,String remapped){
            this.fdMap.put(src,map(owner) + "." +remapped);
        }

        public void addMdRemap(String src, String owner, String remapped){
            this.mdMap.put(src,map(owner) + "." +remapped);
        }

        private void createIfAbsent(File file) throws IOException {
            if (!file.exists()){
                file.createNewFile();
            }
        }

        @Override
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

        @Override
        public String mapFieldName(String owner, String name, String desc, int access) {
            String fieldMapName = this.getFieldMapName(owner + "." + name);
            if (fieldMapName == null) {
                for(String superName = this.superClassMap.get(owner); superName != null && !superName.equals("java/lang/Object"); superName = this.superClassMap.get(superName)) {
                    fieldMapName = this.getFieldMapName(superName + "." + name);
                    if (fieldMapName != null) {
                        break;
                    }
                }
            }

            String src = owner + "." + name;
            if (fieldMapName != null) {
                if (!fdMap.containsKey(src)){
                    if (logRemap){
                        System.out.println("   " + owner + "." + name +  " -> " + fieldMapName);
                    }
                    fdMap.put(src,map(owner) + "." +fieldMapName);
                }
                return fieldMapName;
            }else {
                if (!fdMap.containsKey(src)){
                    if (logRemap) {
                        System.out.println("   " + owner + "." + name +  " -> " + name);
                    }
                    fdMap.put(src,map(owner) + "." +name);
                }
                return name;
            }
        }

        @Override
        public String mapMethodName(String owner, String name, String desc, int access){
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
            String src = owner + "." + name + desc;
            if (methodMapName != null) {
                if (!mdMap.containsKey(src)){
                    if (logRemap){
                        System.out.println("   " + owner + "." + name +  " -> " + methodMapName);
                    }
                    mdMap.put(src,map(owner) + "." + methodMapName);
                    this.addMethodName(owner,name,desc,access,methodMapName);
                }

                return methodMapName;
            }
            else {
                if (!mdMap.containsKey(src)){
                    if (logRemap){
                        System.out.println("   " + owner + "." + name +  " -> " + name);
                    }
                    mdMap.put(src,map(owner) + "." + name);
                }
                return name;
            }
        }

        public void setLogRemap(boolean logRemap) {
            this.logRemap = logRemap;
        }

        public void writeMappings() throws IOException {
            fdMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((stringStringEntry)->{
                try {
                    this.fdWriter.write(stringStringEntry.getKey() + " : " + stringStringEntry.getValue() + "\n");
                    this.fdWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            mdMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(stringStringEntry -> {
                try {
                    this.mdWriter.write(stringStringEntry.getKey() + " : " + stringStringEntry.getValue() + "\n");
                    this.mdWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            this.classMapping.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(stringStringEntry -> {
                try {
                    this.clWriter.write(stringStringEntry.getKey() + " : " + stringStringEntry.getValue() + "\n");
                    this.clWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
