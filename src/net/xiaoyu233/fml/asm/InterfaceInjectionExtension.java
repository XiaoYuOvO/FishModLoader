package net.xiaoyu233.fml.asm;

import com.google.common.collect.Lists;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.api.TrClass;
import net.xiaoyu233.fml.FishModLoader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class InterfaceInjectionExtension implements TinyRemapper.Extension {
    private final Set<InterfaceInjection> injections;

    public InterfaceInjectionExtension(Set<InterfaceInjection> injections) {
        this.injections = injections;
    }

    @Override
    public void attach(TinyRemapper.Builder builder) {
        builder.extraPostApplyVisitor(new TinyRemapper.ApplyVisitorProvider() {
            @Override
            public ClassVisitor insertApplyVisitor(TrClass cls, ClassVisitor next) {
                return new ClassVisitor(Opcodes.ASM9, next) {
                    @Override
                    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                        ArrayList<String> interfaceList = Lists.newArrayList(interfaces);
                        for (InterfaceInjection interfaceInjection : injections) {
                            Collection<Class<?>> injectingClasses = interfaceInjection.getInjections().get(name.replace("/", "."));
                            for (Class<?> injectingClass : injectingClasses) {
                                String fileClassName = injectingClass.getName().replace(".", "/");
                                if (!interfaceList.contains(fileClassName)) {
                                    interfaceList.add(fileClassName);
                                    FishModLoader.LOGGER.info("    [InterfaceInjection] Injecting " + name + " with " + fileClassName + " by: " + interfaceInjection.getModId());
                                }
                            }
                        }
                        super.visit(version, access, name, signature, superName, interfaceList.toArray(new String[0]));
                    }
                };
            }
        });
    }
}
