package net.xiaoyu233.fml.classloading;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.xiaoyu233.fml.AbstractMod;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.config.InjectionConfig;
import org.spongepowered.asm.mixin.MixinEnvironment;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModsWalker {
    private final ClassLoader classLoader;
    private final File modDir;
    public ModsWalker(File modDir, ClassLoader classLoader) throws ClassNotFoundException {
        this.modDir = modDir;
        this.classLoader = classLoader;
        if (!modDir.exists()){
            if (!modDir.mkdirs()) {
                FishModLoader.LOGGER.error("Cannot make mods dir");
            }
        }
    }

    public List<ModFile> findMods(Consumer<File> callback) {
        List<ModFile> result = new ArrayList<>();
        File[] files = this.modDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files != null) {
            for (File file : Lists.newArrayList(files) ){
                try {
                    JarFile jarFile;
                    jarFile = new JarFile(file);
                    JarEntry jarEntry = jarFile.getJarEntry("mod.json");
                    if (jarEntry != null) {
                        JsonElement parse = new JsonParser().parse(new InputStreamReader(jarFile.getInputStream(jarEntry)));
                        if (parse instanceof JsonObject) {
                            String mod = parse.getAsJsonObject().get("mod").getAsString();
                            callback.accept(file);
                            result.add(new ModFile(jarFile, mod,file));
                        }else {
                            FishModLoader.LOGGER.error("Corrupted mod json file,not json object:" + file);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public List<InjectionConfig> loadMods(@Nonnull List<ModFile> modFiles, BiConsumer<AbstractMod, MixinEnvironment.Side[]> callback) throws IOException {
        List<InjectionConfig> result = new ArrayList<>();
        for (ModFile jarsFile : modFiles) {
            Object o;
            Class<?> aClass;
            try {
                aClass = classLoader.loadClass(jarsFile.getModClassName());
                o = aClass.getConstructor().newInstance();
                if (o instanceof AbstractMod) {
                    MixinEnvironment.Side[] modSides = aClass.getDeclaredAnnotation(Mod.class).value();
                    for (MixinEnvironment.Side modSide : modSides) {
                        if (modSide.name().equals(FishModLoader.getSide().name())) {
                            AbstractMod mod = ((AbstractMod) o);
                            callback.accept(mod, modSides);
                            result.add(mod.getInjectionConfig());
                            break;
                        }
                    }
                }
            } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    static class ModFile {
        private final File jar;
        private final JarFile jarFile;
        private final String modClassName;

        ModFile(JarFile jarFile, String modClassName,File jar) {
            this.jarFile = jarFile;
            this.modClassName = modClassName;
            this.jar = jar;
        }

        public File getJar() {
            return jar;
        }

        public JarFile getJarFile() {
            return jarFile;
        }

        public String getModClassName() {
            return modClassName;
        }
    }

//    private List<MixinEnvironment.Side> getSideFromClass(Class<?> cls){
//        Proxy declaredAnnotation = (Proxy) cls.getDeclaredAnnotation(modAnnotationClass);
//        Class<? extends Proxy> aClass = declaredAnnotation.getClass();
//        try {
//            Method h = aClass.getMethod("value");
//            h.setAccessible(true);
//            List<MixinEnvironment.Side> sides = new ArrayList<>();
//            Object invoke = h.invoke(declaredAnnotation);
//            for (int i = 0; i < Array.getLength(invoke); i++) {
//                Object cast = sideClass.cast(Array.get(invoke, i));
//                Class<? extends MixinEnvironment.Side> aClass1 = (Class<? extends MixinEnvironment.Side>) cast.getClass();
//                sides.add(MixinEnvironment.Side.valueOf((String) aClass1.getMethod("name").invoke(cast)));
////                sides.add(cast);
//            }
//            return sides;
//        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
