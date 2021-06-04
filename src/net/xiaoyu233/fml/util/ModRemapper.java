package net.xiaoyu233.fml.util;

import net.xiaoyu233.fml.mapping.Remapping;
import net.xiaoyu233.fml.mapping.Renamer;
import net.xiaoyu233.fml.relaunch.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModRemapper {
    public static final File deobfClassOut = new File("D:\\DeobfOut");

    public static void main(String[] args) throws IOException {
        Remapping remapping = new ModRemapping();
        remapping.addMappingFromStream(Launch.class.getResourceAsStream("/class.mapping"), Remapping.MappingType.CLASS);
        remapping.addMappingFromStream(Launch.class.getResourceAsStream("/method.mapping"), Remapping.MappingType.METHOD);
        remapping.addMappingFromStream(Launch.class.getResourceAsStream("/field.mapping"), Remapping.MappingType.FIELD);
        Renamer renamer = new Renamer(remapping);
        String path = args[0];
        if (path.endsWith(".jar")){
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> entries = jarFile.entries();
            Map<String, MappingGenerator.ClassData> classMap = new HashMap<>();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")){
                    InputStream inputStream = jarFile.getInputStream(entry);
                    byte[] basicClass = Utils.readAllBytes(inputStream);
                    ClassReader reader = new ClassReader(basicClass);
                    ClassNode classNode = new ClassNode();
                    reader.accept(classNode,0);
                    remapping.addSuperclassMapping(classNode.name, classNode.superName);
                    remapping.addInterfaceMap(classNode.name, classNode.interfaces);
                    classMap.put(classNode.name,new MappingGenerator.ClassData(classNode,basicClass));
                }
            }
            jarFile.close();
            for (Map.Entry<String, MappingGenerator.ClassData> stringClassDataEntry : classMap.entrySet()) {
                File file;
                MappingGenerator.ClassData value = stringClassDataEntry.getValue();
                ClassNode classNode = value.getClassNode();
                String mapped = remapping.map(classNode.name);
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
                    ClassWriter cw = new ClassWriter(0);
                    remapAnnotations(classNode,remapping).accept(cw);
                    fileOutputStream.write(renamer.transform(classNode.name, mapped, cw.toByteArray()));
                }
            }
        }
    }

    private static ClassNode remapAnnotations(ClassNode classNode,Remapping remapping){
        String targetName = null;
        if (classNode.visibleAnnotations != null){
            for (AnnotationNode visibleAnnotation : classNode.visibleAnnotations) {
                if (visibleAnnotation.desc.contains("Transform")) {
                    targetName = (((Type) visibleAnnotation.values.get(1)).getClassName().replace(".", "/"));
                }
            }
            if (targetName != null){
                for (FieldNode field : classNode.fields) {
                    List<AnnotationNode> visibleAnnotations = field.visibleAnnotations;
                    if (visibleAnnotations != null) {
                        if (visibleAnnotations.stream().anyMatch(annotationNode -> annotationNode.desc.contains("Link"))) {
                            String rawName = field.name;
                            field.name = remapping.mapFieldName(targetName, rawName,remapping.unmapDesc(field.desc));
                            System.out.println("Remapped target field: " + rawName + " -> " + field.name + " | in " + targetName);
                            remapping.addFieldName(classNode.name,rawName,field.desc,field.access,field.name);
                        }
                    }
                }
                for (MethodNode method : classNode.methods) {
                    String rawName = method.name;
                    method.name = remapping.mapMethodName((targetName), rawName, remapping.unmapDesc(method.desc));
                    System.out.println("Remapped target method: " + rawName + " -> " + method.name + " | in " + targetName);
                    remapping.addMethodName(classNode.name,rawName,method.desc,method.access,method.name);
                }
            }
        }
        return classNode;
    }

    static class ModRemapping extends Remapping{
        public void addFieldName(String owner, String name, String desc, int access, String newName) {
            this.fieldMapping.put(owner + "." + name + unmapDesc(desc), newName);
        }

        @Override
        public void addMethodName(String owner, String name, String desc, int access, String newName) {
            this.methodMapping.put(owner + "." + name + unmapDesc(desc), newName);
        }

        @Override
        public String mapFieldName(String owner, String name, String desc, int access) {
            String fieldMapName;
            if(owner.startsWith("net/minecraft")){
                fieldMapName = this.getFieldMapName(unmap(owner) + "." + name);
            }else {
                fieldMapName = this.getFieldMapName(owner + "." + name);
                if (fieldMapName == null) {
                    for(String superName = this.superClassMap.get(owner); superName != null && !superName.equals("java/lang/Object"); superName = this.superClassMap.get(superName)) {
                        fieldMapName = this.getFieldMapName(unmap(superName) + "." + name + unmapDesc(desc));
                        if (fieldMapName != null) {
                            break;
                        }
                    }
                }
            }
            if (fieldMapName == null) {
                return name;
            }
            return fieldMapName;
        }

        public String mapMethodName(String owner, String name, String desc, int access) {
            String methodMapName;
            if (owner.startsWith("net/minecraft")){
                methodMapName = this.getMethodMapName(unmap(owner) + "." + name, unmapDesc(desc));
            }else {
                methodMapName = this.getMethodMapName((owner) + "." + name, unmapDesc(desc));
                if (methodMapName == null) {
                    for(String superName = this.superClassMap.get(owner); superName != null && !superName.equals("java/lang/Object"); superName = this.superClassMap.get(superName)) {
                        methodMapName = this.getMethodMapName(unmap(superName) + "." + name, unmapDesc(desc));
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
            }
            if (methodMapName == null) {
                return name;
            }
            return methodMapName;
        }
    }
}
