package net.xiaoyu233.fml.asm;

import com.google.common.collect.ArrayListMultimap;
import net.xiaoyu233.fml.util.Utils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class Transformer {
    private boolean mappingOnly;
    private HashMap<String, String> transformMap = new HashMap<>();
    private HashMap<String, String> fieldOwnerMap = new HashMap<>();
    private HashMap<String, String> methodOwnerMap = new HashMap<>();
    private ArrayListMultimap<String, FieldWrapper> fieldAddMap = ArrayListMultimap.create();
    private ArrayListMultimap<String, MethodWrapper> methodAddMap = ArrayListMultimap.create();
    private HashMap<String, String> linkFieldMap = new HashMap<>();

    public Transformer(boolean var1) {
        this.mappingOnly = var1;
    }

    public byte[] transform(byte[] var1) {
        var1 = this.mapping(var1);
        if (this.mappingOnly) {
            return var1;
        } else {
            final ClassReader var2 = new ClassReader(var1);
            ClassNode var3 = new ClassNode();
            var2.accept(var3, 0);
            String var4 = var2.getClassName();
            if (this.transformMap.containsKey(var4)) {
                this.interfaced(var3);
            } else {
                List<String> var5 = this.transformMap.entrySet().stream().filter((var1x) -> var1x.getValue().equals(var4)).map(Map.Entry::getKey).collect(Collectors.toList());
                if (!var5.isEmpty()) {
                    if (var3.interfaces != null) {
                        var3.interfaces.addAll(var5);
                    } else {
                        var3.interfaces = var5;
                    }
                }

                List<FieldWrapper> var6 = this.fieldAddMap.get(var4);
//                for (FieldNode var9 : var3.fields) {
//                    for (FieldWrapper var11 : var6) {
//                        if (var11.fieldName.equals(var9.name)) {
//                            var7.add(var11);
//                        }
//                    }
//                }
//
//                var6.removeAll(var7);
                var6.removeIf(var1x -> {
                    for (FieldNode field : var3.fields) {
                        if (field.name.equals(var1x.fieldName)){
                            return true;
                        }
                    }
                    return false;
                });
                var6.forEach((var1x) -> var3.fields.add(new FieldNode(var1x.opcode, var1x.fieldName, var1x.desc, var1x.signature, var1x.value)));

                //Transform methods
                List<MethodWrapper> var14 = this.methodAddMap.get(var4);

                for (MethodNode var16 : var3.methods) {
                    MethodWrapper var17 = new MethodWrapper(var16.name, var16.desc, null);
                    if (var14.contains(var17)) {
                        MethodWrapper var12 = this.getMethodWrapper(var14, var17);
                        var16.instructions.clear();
                        var16.instructions.add(var12.node.instructions);
                        var16.maxStack = var12.node.maxStack;
                        var16.maxLocals = var12.node.maxLocals;
                        var14.remove(var17);
                        var16.tryCatchBlocks = var12.node.tryCatchBlocks;
                        var16.localVariables = var12.node.localVariables;
                    }
                }

                var14.forEach((var1x) -> {
                    MethodNode methodNode = new MethodNode(var1x.node.access, var1x.methodName, var1x.desc, var1x.node.signature,
                            var1x.node.exceptions.toArray(new String[0]));
                    methodNode.instructions.add(var1x.node.instructions);
                    var3.methods.add(methodNode);
                    methodNode.maxStack = var1x.node.maxStack;
                    methodNode.maxLocals = var1x.node.maxLocals;
                    methodNode.tryCatchBlocks = var1x.node.tryCatchBlocks;
                    methodNode.localVariables = var1x.node.localVariables;
                });
            }

            ClassWriter var13 = new ClassWriter(0);
            var3.accept(new ClassVisitor(458752, var13) {
                public MethodVisitor visitMethod(int var1, String var2x, String var3, String var4, String[] var5) {
                    Transformer var10002 = Transformer.this;
                    this.getClass();
                    return var10002.new ModMethodVisitor(var2.getClassName(), var2x, var3, super.visitMethod(var1, var2x, var3, var4, var5));
                }
            });
            return var13.toByteArray();
        }
    }

    private MethodWrapper getMethodWrapper(List<MethodWrapper> var1, MethodWrapper var2) {
        Iterator<MethodWrapper> var3 = var1.iterator();

        MethodWrapper var4;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            var4 = var3.next();
        } while(!var4.equals(var2));

        return var4;
    }

    private void interfaced(ClassNode var1) {
        MethodNode var3;
        for(Iterator<MethodNode> var2 = var1.methods.iterator(); var2.hasNext(); var3.access = 0x401) {
            var3 = var2.next();
            var3.instructions.clear();
        }

        var1.fields.clear();
        var1.access = 0x601;
        var1.superName = "java/lang/Object";
        if (var1.interfaces != null) {
            var1.interfaces.clear();
        }

    }

    private byte[] mapping(byte[] var1) {
        ClassNode var2 = new ClassNode();
        ClassReader var3 = new ClassReader(var1);
        var3.accept(var2, 0);
        var2.name = Mapping.getClassMapName(var2.name.replace('/', '.')).replace('.', '/');

        MethodNode var5;
        for (MethodNode methodNode : var2.methods) {
            var5 = methodNode;
            var5.name = Mapping.getMethodMapName(var2.name + "." + var5.name, var5.desc);
        }

        FieldNode var7;
        for(Iterator variterator = var2.fields.iterator(); variterator.hasNext(); var7.name = Mapping.getFieldMapName(var2.name + "." + var7.name)) {
            var7 = (FieldNode)variterator.next();
        }

        ClassWriter var6 = new ClassWriter(0);
        var2.accept(new ClassVisitor(458752, var6) {
            public FieldVisitor visitField(int var1, String var2, String var3, String var4, Object var5) {
                return super.visitField(var1, var2, Transformer.this.replaceFieldDesc(var3), var4, var5);
            }

            public MethodVisitor visitMethod(int var1, String var2, String var3, String var4, String[] var5) {
                Transformer var10002 = Transformer.this;
                this.getClass();
                return var10002.new MappingMethodVisitor(super.visitMethod(var1, var2, Transformer.this.replaceMethodDesc(var3), var4, Transformer.this.transformInternalArray(var5)));
            }

            public void visit(int var1, int var2, String var3, String var4, String var5, String[] var6) {
                super.visit(var1, var2, var3, var4, Mapping.getClassMapName(var5.replace('/', '.')).replace('.', '/'), Transformer.this.transformInternalArray(var6));
            }
        });
        return var6.toByteArray();
    }

    private String[] transformInternalArray(String[] var1) {
        ArrayList<String> var2 = new ArrayList<>();
        String[] var3 = var1;
        int var4 = var1.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            var2.add(Mapping.getClassMapName(var6.replace('/', '.')).replace('.', '/'));
        }

        return var2.toArray(new String[0]);
    }

    private String replaceMethodDesc(String var1) {
        String var2 = var1.substring(var1.indexOf(41) + 1);
        var2 = Transformer.this.replaceFieldDesc(var2);
        Type[] var3 = Type.getArgumentTypes(var1);
        StringBuilder var4 = new StringBuilder();
        Type[] var5 = var3;
        int var6 = var3.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Type var8 = var5[var7];
            var4.append(Transformer.this.replaceFieldDesc(var8.getDescriptor()));
        }

        return "(" + var4.toString() + ")" + var2;
    }

    private String replaceFieldDesc(String var1) {
        if (Utils.isJavaType(var1)) {
            return var1;
        } else {
            int var2 = 0;

            while(true) {
                char var3 = var1.charAt(var2);
                if (var3 != '[' && var3 != 'L') {
                    String var4 = var1.substring(var2, var1.length() - 1);
                    String var5 = Mapping.getClassMapName(var4.replace("/", "."));
                    return var1.substring(0, var2) + var5.replace(".", "/") + ";";
                }

                ++var2;
            }
        }
    }

    public void addClassTransform(String var1, String var2) {
        this.transformMap.put(var2, var1);
    }

    public void addMethodTransform(String var1, String var2, String var3, String var4, MethodNode var5) {
        this.addOrOverrideMethod(var1, var3, var4, var5);
        this.addMethodVisitor(var2, var1);
    }

    private void addMethodVisitor(String var1, String var2) {
        this.methodOwnerMap.put(var1, var2);
    }

    private void addOrOverrideMethod(String var1, String var2, String var3, MethodNode var4) {
        this.methodAddMap.put(var1, new MethodWrapper(var2, var3, var4));
    }

    public void addFieldTransform(String var1, String var2, int var3, String var4, String var5, String var6, String var7, Object var8) {
        this.addFiled(var1, var3, var4, var5, var6, var7, var8);
        this.addFiledVisitor(var2, var1);
        this.linkFieldMap.put(var1 + "/" + var5, var4);
    }

    private void addFiledVisitor(String var1, String var2) {
        this.fieldOwnerMap.put(var1, var2);
    }

    private void addFiled(String var1, int var2, String var3, String var4, String var5, String var6, Object var7) {
        this.fieldAddMap.put(var1, new FieldWrapper(var2, var3, var4, var5, var6, var7));
    }

    private static class MethodWrapper {
        public final String methodName;
        public final String desc;
        public final MethodNode node;

        public MethodWrapper(String var2, String var3, MethodNode var4) {
            this.methodName = var2;
            this.desc = var3;
            this.node = var4;
        }

        public boolean equals(Object var1) {
            if (this == var1) {
                return true;
            } else if (var1 != null && this.getClass() == var1.getClass()) {
                MethodWrapper var2 = (MethodWrapper)var1;
                return Objects.equals(this.methodName, var2.methodName) && Objects.equals(this.desc, var2.desc);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(this.methodName, this.desc);
        }
    }

    private class MappingMethodVisitor extends MethodVisitor {
        public MappingMethodVisitor(MethodVisitor var2) {
            super(458752, var2);
        }

        public void visitFieldInsn(int var1, String var2, String var3, String var4) {
            super.visitFieldInsn(var1, Mapping.getClassMapName(var2.replace('/', '.')).replace('.', '/'), var3, Transformer.this.replaceFieldDesc(var4));
        }

        public void visitParameter(String var1, int var2) {
            super.visitParameter(var1, var2);
        }

        public void visitMethodInsn(int var1, String var2, String var3, String var4, boolean var5) {
            if (var2.startsWith("[")) {
                var2 = Transformer.this.replaceFieldDesc(var2);
            } else {
                var2 = Mapping.getClassMapName(var2.replace('/', '.')).replace('.', '/');
            }

            super.visitMethodInsn(var1, var2, var3, Transformer.this.replaceMethodDesc(var4), var5);
        }
        
        public void visitTypeInsn(int var1, String var2) {
            if (var2.startsWith("[")) {
                super.visitTypeInsn(var1, Transformer.this.replaceFieldDesc(var2));
            } else {
                super.visitTypeInsn(var1, Mapping.getClassMapName(var2.replace('/', '.')).replace('.', '/'));
            }

        }

        public void visitLocalVariable(String var1, String var2, String var3, Label var4, Label var5, int var6) {
            super.visitLocalVariable(var1, Transformer.this.replaceFieldDesc(var2), var3, var4, var5, var6);
        }

        public void visitFrame(int var1, int var2, Object[] var3, int var4, Object[] var5) {
            if (var3 != null) {
                this.mappingFrame(var3);
            }

            if (var5 != null) {
                this.mappingFrame(var5);
            }

            super.visitFrame(var1, var2, var3, var4, var5);
        }

        private void mappingFrame(Object[] var1) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
                Object var3 = var1[var2];
                if (var3 instanceof String) {
                    String var4 = (String)var3;
                    if (var4.endsWith(";")) {
                        var1[var2] = Transformer.this.replaceFieldDesc(var4);
                    } else {
                        int var5 = 0;

                        while(true) {
                            char var6 = var4.charAt(var5);
                            if (var6 != '[') {
                                String var7 = var4.substring(var5);
                                var1[var2] = this.getLeftArraySymbol(var5) + Mapping.getClassMapName(var7.replace('/', '.')).replace('.', '/');
                                break;
                            }

                            ++var5;
                        }
                    }
                }
            }

        }

        private String getLeftArraySymbol(int var1) {
            char[] var2 = new char[var1];
            Arrays.fill(var2, '[');
            return new String(var2);
        }

        public void visitTryCatchBlock(Label var1, Label var2, Label var3, String var4) {
            super.visitTryCatchBlock(var1, var2, var3, var4 != null ? Mapping.getClassMapName(var4.replace('/', '.')).replace('.', '/') : null);
        }

        public void visitMultiANewArrayInsn(String var1, int var2) {
            super.visitMultiANewArrayInsn(Transformer.this.replaceFieldDesc(var1), var2);
        }

        public void visitInvokeDynamicInsn(String var1, String var2, Handle var3, Object... var4) {
            super.visitInvokeDynamicInsn(var1, var2, var3, var4);
        }

        public void visitLdcInsn(Object var1) {
            if (var1 instanceof Type) {
                Type var2 = (Type)var1;
                if (10 == var2.getSort()) {
                    super.visitLdcInsn(Type.getType(Transformer.this.replaceFieldDesc(var2.getDescriptor())));
                }
            } else {
                super.visitLdcInsn(var1);
            }

        }
    }

    private class ModMethodVisitor extends MethodVisitor {
        private final String className;
        private final String methodName;
        private final String desc;

        public ModMethodVisitor(String var2, String var3, String var4, MethodVisitor var5) {
            super(458752, var5);
            this.className = var2;
            this.methodName = var3;
            this.desc = var4;
        }

        public void visitFieldInsn(int var1, String var2, String var3, String var4) {
            if (Transformer.this.transformMap.containsKey(var2)) {
                if (Transformer.this.transformMap.get(var2).equals(this.className)) {
                    super.visitFieldInsn(var1, Transformer.this.transformMap.get(var2),
                            Transformer.this.linkFieldMap.get(
                                    Transformer.this.transformMap.get(var2) + "/" + var3), var4);
                } else {
                    super.visitFieldInsn(var1, var2, var3, var4);
                }
            } else {
                super.visitFieldInsn(var1, var2, var3, var4);
            }

        }

        public void visitMethodInsn(int var1, String var2, String var3, String var4, boolean var5) {
            if (Transformer.this.transformMap.containsKey(var2)) {
                if (!Transformer.this.transformMap.get(var2).equals(this.className)) {
                    if (var1 == INVOKEVIRTUAL) {
                        super.visitMethodInsn(INVOKEINTERFACE, var2, var3, var4, true);
                    } else if (var1 == INVOKESPECIAL && var3.equals("<init>")) {
                        super.visitMethodInsn(INVOKEINTERFACE, Transformer.this.transformMap.get(var2), var3, var4, false);
                    } else {
                        super.visitMethodInsn(var1, Transformer.this.transformMap.get(var2), var3, var4, var5);
                    }
                } else {
                    super.visitMethodInsn(var1, Transformer.this.transformMap.get(var2), var3, var4, var5);
                }
            } else {
                super.visitMethodInsn(var1, var2, var3, var4, var5);
            }

        }
    }

    private static class FieldWrapper {
        public final int opcode;
        public final String fieldName;
        public final String linkedName;
        public final String desc;
        public final String signature;
        public final Object value;

        public FieldWrapper(int var2, String var3, String var4, String var5, String var6, Object var7) {
            this.opcode = var2;
            this.fieldName = var3;
            this.linkedName = var4;
            this.desc = var5;
            this.signature = var6;
            this.value = var7;
        }
    }
}
