package net.xiaoyu233.fml.classloading;

import com.chocohead.mm.AsmTransformer;
import net.fabricmc.accesswidener.AccessWidenerClassVisitor;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.classloading.dump.ClassDumper;
import net.xiaoyu233.fml.classloading.dump.DumpStage;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Optional;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.ASM9;

public class FMLClassTransformer {

    public static byte[] transform(String name, byte[] bytes, AsmTransformer asmTransformer) {
        boolean isMinecraftClass = name.startsWith("net.minecraft.") || name.startsWith("com.mojang.blaze3d.") || name.indexOf('.') < 0;
        Optional<Consumer<ClassNode>> classModifier = asmTransformer.getClassModifier(name);
        boolean hasTransformation = classModifier.isPresent();
        boolean applyAccessWidener = isMinecraftClass && FishModLoader.getAccessWidener().getTargets().contains(name) ;

        if (!applyAccessWidener && !hasTransformation) {
            return bytes;
        }

        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        ClassNode classNode = new ClassNode();
        // If we have the transformations, we need the class info goto the nodes so we can use it, else just let it go to writer to write
        ClassVisitor visitor = hasTransformation ? classNode : classWriter;

        if (applyAccessWidener) {
            visitor = AccessWidenerClassVisitor.createClassVisitor(ASM9, visitor, FishModLoader.getAccessWidener());
            FishModLoader.LOGGER.info("[AW] Widened class: " + name);
        }
        classReader.accept(visitor, 0);
        if (hasTransformation){
            if (applyAccessWidener){
                //Dump before CT
                ClassDumper.dumpClassStaged(classNode,name, DumpStage.ACCESS_WIDENER);
            }
            classModifier.ifPresent(modifier -> modifier.accept(classNode));
            //Write the modified class data to the writer
            classNode.accept(classWriter);
        }

        byte[] byteArray = classWriter.toByteArray();
        if (hasTransformation){
            //Final stage for has class tinkers and has both CT and AW
            ClassDumper.dumpClassStaged(byteArray, name, DumpStage.CLASS_TINKER);
        }else{
            //Final stage for only AW
            ClassDumper.dumpClassStaged(byteArray, name, DumpStage.ACCESS_WIDENER);
        }
        return byteArray;
    }
}
