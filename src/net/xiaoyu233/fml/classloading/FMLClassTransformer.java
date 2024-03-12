package net.xiaoyu233.fml.classloading;

import net.fabricmc.accesswidener.AccessWidenerClassVisitor;
import net.xiaoyu233.fml.FishModLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class FMLClassTransformer {
    public static byte[] transform(boolean isDevelopment, String name, byte[] bytes) {
        boolean isMinecraftClass = name.startsWith("net.minecraft.") || name.startsWith("com.mojang.blaze3d.") || name.indexOf('.') < 0;
        boolean environmentStrip = !isMinecraftClass || isDevelopment;
        boolean applyAccessWidener = isMinecraftClass && FishModLoader.getAccessWidener().getTargets().contains(name);

        if (!environmentStrip && !applyAccessWidener) {
            return bytes;
        }

        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        ClassVisitor visitor = classWriter;
        int visitorCount = 0;

        if (applyAccessWidener) {
            visitor = AccessWidenerClassVisitor.createClassVisitor(Opcodes.ASM9, visitor, FishModLoader.getAccessWidener());
            FishModLoader.LOGGER.info("[AW] Widened class: " + name);
            visitorCount++;
        }


        if (visitorCount <= 0) {
            return bytes;
        }

        classReader.accept(visitor, 0);
        return classWriter.toByteArray();
    }
}
