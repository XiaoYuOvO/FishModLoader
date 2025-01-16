package net.xiaoyu233.fml.classloading.dump;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.config.Configs;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ClassDumper {

    public static void dumpClassStaged(ClassNode classNode, String className, DumpStage stage){
        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        dumpClassStaged(writer.toByteArray(), className, stage);
    }

    public static void dumpClassStaged(byte[] classNode, String className, DumpStage stage){
        if (!Configs.Debug.DEBUG.get()) return;
        if (!stage.shouldDump()) return;
        if (!className.startsWith(Configs.Debug.DumpClass.DUMP_PREFIX.get())) return;
        className = className.replace(".","/");
        Path dumpPath = Configs.Debug.DumpClass.DUMP_PATH.get().toPath().resolve(stage.name().toLowerCase()).resolve(className + ".class");
        try {
            ensureDumpDir(dumpPath.getParent());
            Files.write(dumpPath, classNode, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            FishModLoader.LOGGER.error("Cannot dump class {} to {}", className, dumpPath, e);
        }
    }

    private static void ensureDumpDir(Path path) throws IOException {
        if (Files.exists(path)) {
            return;
        }
        Files.createDirectories(path);
    }
}
