package net.xiaoyu233.fml.asm;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.xiaoyu233.fml.AbstractMod;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.annotations.*;
import net.xiaoyu233.fml.classloading.ModClassLoader;
import net.xiaoyu233.fml.classloading.StaticClassLoader;
import net.xiaoyu233.fml.util.ModInfo;
import net.xiaoyu233.fml.util.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class ModsWalker {
    private ModClassLoader classLoader;
    private Transformer transformer;
    private File modFolder;
    private String jarPath;
    private String outputFile;
    private boolean staticMod;
    private boolean mappingOnly;
    private final ArrayList<Runnable> postInits = new ArrayList<>();
    public static boolean debug = false;

    private ModsWalker() {
        this.modFolder = new File("CoreMod");
        this.staticMod = false;
        this.mappingOnly = false;
    }

    private void init() {
        Mapping.loadMappingFromJar();
        this.transformer = new Transformer(this.mappingOnly);

        try {
            if (this.staticMod) {
                StaticClassLoader staticClassLoader = new StaticClassLoader(new File(this.jarPath), new File(this.outputFile), this.transformer);
                staticClassLoader.start();
            } else {
                this.classLoader = new ModClassLoader(new File(this.jarPath), this.transformer);
            }
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        Thread.currentThread().setContextClassLoader(this.classLoader);
        if (!this.modFolder.exists()) {
            this.modFolder.mkdirs();
        }

        if (!this.staticMod) {
            this.registerMods(this.modFolder);
        }

    }

    private void registerMods(File modPath) {
        if (!modPath.exists()) {
            modPath.mkdirs();
        }

        File[] var2 = modPath.listFiles();
        if (var2 != null) {
            this.loadInternalModInject();
            for (File modFile : var2) {
                System.out.println("found mod " + modFile.getName());
                if (modFile.getName().endsWith(".jar")) {
                    try {
                        JsonElement jsonObject = null;
                        JarFile jarFile = new JarFile(modFile);
                        Enumeration<JarEntry> entries = jarFile.entries();
                        HashMap<String, JarEntry> entryHashMap = new HashMap<>();

                        while (entries.hasMoreElements()) {
                            JarEntry jarEntry = entries.nextElement();
                            if (!jarEntry.isDirectory()) {
                                if (jarEntry.getName().endsWith("mod.json")) {
                                    InputStream source = jarFile.getInputStream(jarEntry);
                                    Throwable var12 = null;

                                    try {
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(source));
                                        jsonObject = (new JsonParser()).parse(reader);
                                    } catch (Throwable var22) {
                                        var12 = var22;
                                        throw var22;
                                    } finally {
                                        if (source != null) {
                                            if (var12 != null) {
                                                try {
                                                    source.close();
                                                } catch (Throwable var23) {
                                                    var12.addSuppressed(var23);
                                                }
                                            } else {
                                                source.close();
                                            }
                                        }

                                    }
                                } else if (jarEntry.getName().endsWith(".class")) {
                                    entryHashMap.put(jarEntry.getName(), jarEntry);
                                }
                            }
                        }

                        if (jsonObject == null) {
                            continue;
                        }
                        String modClassLocation = null;
                        if (jsonObject.getAsJsonObject().has("mod")) {
                            modClassLocation = jsonObject.getAsJsonObject().get("mod").getAsString();
                        }else{
                            System.err.println("Bad Mod File:" + modFile.getAbsolutePath() + " ,mod.json is incorrect");
                        }
                        if (modClassLocation == null || modClassLocation.isEmpty()) {
                            continue;
                        }
                        String finalModClassLocation = modClassLocation;
                        entryHashMap.entrySet().stream().filter((jarEntries) -> jarEntries.getKey().startsWith(
                                finalModClassLocation.replace('.', '/'))).forEach((jarEntries) -> {
                            String modInjectPath;
                            try {
                                InputStream inputStream = jarFile.getInputStream(jarEntries.getValue());

                                try {
                                    byte[] classData = Utils.readAllBytes(inputStream);
                                    ClassReader reader = new ClassReader(classData);
                                    ClassNode classNode = new ClassNode();
                                    reader.accept(classNode, 0);

                                    for (AnnotationNode annotationNodexx : classNode.visibleAnnotations) {
                                        if (annotationNodexx.desc.equals(Type.getDescriptor(Mod.class))) {
                                            Object modClass;
                                            try {
                                                 modClass = this.classLoader.defineClass1(classNode.name.replace("/", "."),
                                                        classData, 0, classData.length).newInstance();
                                            }catch (LinkageError error){
                                                System.err.println("Duplicate mod file:" + jarFile.getName() + " ;with mod main class: " + classNode.name);
                                                continue;
                                            }
                                            if (modClass instanceof AbstractMod) {
                                                AbstractMod mod = (AbstractMod)modClass;
                                                mod.preInit();
                                                postInits.add(mod::postInit);
                                                modInjectPath = mod.transformPkg();
                                                Mod[] annotations = mod.getClass().getDeclaredAnnotationsByType(Mod.class);
                                                Dist[] dists = new Dist[]{Dist.CLIENT,Dist.SERVER};
                                                boolean inServer = false,inClient = false;
                                                if (annotations.length == 1){
                                                    dists = annotations[0].value();
                                                    for (Dist dist : dists) {
                                                        inServer = inServer || dist.equals(Dist.SERVER);
                                                        inClient = inClient || dist.equals(Dist.CLIENT);
                                                    }
                                                }
                                                //For Client only mods
                                                if ((inClient && !inServer) && FishModLoader.isServer()){
                                                    System.err.println("Cannot load mod:" + jarFile.getName() + ", this mod is for the client only,can't use in server");
                                                }else
                                                    //For Server only mods
                                                if ((!inClient && inServer) && !FishModLoader.isServer()){
                                                    System.err.println("Cannot load mod:" + jarFile.getName() + ", this mod is for the server only,can't use in client");
                                                }else {
                                                    FishModLoader.addModInfo(new ModInfo(mod.modId(),mod.modVerStr(),mod.modVerNum(),dists));
                                                    loadModInject(entryHashMap,modInjectPath,jarFile);
                                                }
                                            } else {
                                                System.err.println("Cannot load mod class:\"" + classNode.name + "\" not a valid mod class(should extend AbstractMod)");
                                            }
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException var25) {
                        var25.printStackTrace();
                    }
                }

                System.out.println("append mod " + modFile.getName());
                this.classLoader.addURL(modFile);
            }

            for (Runnable postInit : postInits) {
                postInit.run();
            }
        }

    }

    private void loadInternalModInject() {
        //All the classes that need to be loaded after launch should be placed under the net.xiaoyu233.fml.reload package
        try {
            File file = Utils.createJar("FML_TRANS");
            System.out.println(file.getAbsolutePath());
            JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(file));
            for (Map.Entry<String,InputStream> internalTransformerClass : Utils.getInternalClassesFromJar("net/xiaoyu233/fml/reload").entrySet()) {
                File classFile = new File(file,internalTransformerClass.getKey().replace(".","/") + ".class");
                classFile.getParentFile().mkdirs();
                outputStream.putNextEntry(new JarEntry(internalTransformerClass.getKey()));
                Utils.copy(internalTransformerClass.getValue(),outputStream);
                internalTransformerClass.getValue().close();
            }
            outputStream.flush();
            outputStream.close();
            for (InputStream internalTransformerClass : Utils.getInternalClassesFromJar("net/xiaoyu233/fml/reload/transform").values()) {
                try {
                    ClassReader reader = new ClassReader(internalTransformerClass);
                    ClassNode classNode = new ClassNode();
                    reader.accept(classNode, 0);
                    this.transformClass(classNode,reader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                internalTransformerClass.close();
            }
            this.classLoader.addURL(file);
            Runtime.getRuntime().addShutdownHook(new Thread(file::delete));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transformClass(ClassNode classNode,ClassReader reader) {
        String target = null;

        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode annotationNodexx : classNode.visibleAnnotations) {
                if (annotationNodexx.desc.equals(Type.getDescriptor(Transform.class))) {
                    target = annotationNodexx.values.get(1).toString();
                    if (target != null) {
                        target = Utils.getInternalNameFromDesc(target);
                    }
                }
            }
        }

        if (debug) {
            if (target == null) {
                System.out.println("target is null");
            } else {
                System.out.println(reader.getClassName() + " -> " + target);
            }
        }

        if (target != null) {
            this.transformer.addClassTransform(target, reader.getClassName());
            this.transformer.addExtendsLink(reader.getClassName(),reader.getSuperName());
            String targetFieldName;
            FieldNode fieldNode;
            if (classNode.fields != null) {
                for (FieldNode node : classNode.fields) {
                    fieldNode = node;
                    targetFieldName = null;

                    if (fieldNode.visibleAnnotations != null) {
                        for (AnnotationNode annotationNode : fieldNode.visibleAnnotations) {
                            if (annotationNode.desc.equals(Type.getDescriptor(Link.class)))
                                if (annotationNode.values != null && !annotationNode.values.get(
                                        1).toString().isEmpty()) {
                                    targetFieldName = annotationNode.values.get(1).toString();
                                }else {
                                    targetFieldName = fieldNode.name;
                                }
                        }
                    }
                    if (targetFieldName != null){
                        if (debug) {
                            System.out.println("    linkField: targetFieldName:" + targetFieldName + " linkedName: " + fieldNode.name);
                        }
                        this.transformer.addFieldLink(target, reader.getClassName(), fieldNode.access, targetFieldName, fieldNode.name, fieldNode.desc, fieldNode.signature, fieldNode.value);
                    }else {
                        if (debug){
                            System.out.println("    addField: " + fieldNode.name);
                        }
                        this.transformer.addField(target, reader.getClassName(), fieldNode.access, fieldNode.name, fieldNode.name, fieldNode.desc, fieldNode.signature, fieldNode.value);
                    }
                }
            }

            for (MethodNode method : classNode.methods) {
                boolean isMarker = false;
                if (method != null){
                    if (method.visibleAnnotations != null){
                        for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
                            if (visibleAnnotation.desc.equals(Type.getDescriptor(Marker.class))){
                                isMarker = true;
                            }
                        }
                    }
                    //Avoid for default constructor
                    if (method.name.equals("<init>") && method.desc.equals("()V") && method.access == Modifier.PUBLIC){
                        isMarker = true;
                    }
                    if (!isMarker){
                        if (debug) {
                            System.out.println("    addMethod: " + method.name);
                        }
                        this.transformer.addMethodTransform(target, reader.getClassName(), method.name, method.desc, method);
                    }else {
                        if (debug) {
                            System.out.println("    markerMethod: " + method.name);
                        }
                    }

                }
            }
        }

    }

    private void loadModInject(HashMap<String, JarEntry> entryHashMap,String modClassLocation,JarFile jarFile){
        entryHashMap.entrySet().stream().filter((jarEntries) -> jarEntries.getKey().startsWith(modClassLocation.replace('.', '/'))).forEach((jarEntries) -> {
            try {
                InputStream inputStream = jarFile.getInputStream(jarEntries.getValue());
                Throwable throwable = null;
                byte[] classData = Utils.readAllBytes(inputStream);
                ClassReader reader = new ClassReader(classData);
                ClassNode classNode = new ClassNode();
                reader.accept(classNode, 0);

                try{
                    this.transformClass(classNode,reader);
                } finally {
                    inputStream.close();
                }

            } catch (IOException var24) {
                var24.printStackTrace();
            }
        });
    }

    public Transformer getTransformer() {
        return this.transformer;
    }

    public static LoadConfig getBuilder(String jarPath) {
        return new LoadConfig(jarPath);
    }

    public static class LoadConfig {
        private final ModsWalker modsWalker = new ModsWalker();

        public LoadConfig(String jarPath) {
            this.modsWalker.jarPath = jarPath;
        }

        public void setModFolder(File file) {
            this.modsWalker.modFolder = file;
        }

        public LoadConfig setStaticMod(boolean staticMod) {
            this.modsWalker.staticMod = staticMod;
            return this;
        }

        public LoadConfig setMappingOnly(boolean mappingOnly) {
            this.modsWalker.mappingOnly = mappingOnly;
            return this;
        }

        public LoadConfig setOutputFile(String outputFile) {
            this.modsWalker.outputFile = outputFile;
            return this;
        }

        public LoadConfig setDebug(boolean debug) {
            ModsWalker.debug = debug;
            return this;
        }

        public ModsWalker build() {
            this.modsWalker.init();
            return this.modsWalker;
        }
    }
}
