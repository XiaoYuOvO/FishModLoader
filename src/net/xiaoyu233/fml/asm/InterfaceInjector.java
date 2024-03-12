package net.xiaoyu233.fml.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.ILegacyClassTransformer;

import java.util.Set;

public class InterfaceInjector implements IClassTransformer, ILegacyClassTransformer {
    private final Set<InterfaceInjection> injectionList;
    public InterfaceInjector(Set<InterfaceInjection> injectionList){
        this.injectionList = injectionList;
    }
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return applyInterfaceInjection(transformedName, basicClass);
    }

    private byte[] applyInterfaceInjection(String transformedName, byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        return applyInterfaceInjection(transformedName, basicClass);
    }

    @Override
    public String getName() {
        return "FMLInterfaceInjection";
    }

    @Override
    public boolean isDelegationExcluded() {
        return true;
    }
}
