package net.xiaoyu233.fml.asm;

import com.google.common.collect.ArrayListMultimap;
import net.xiaoyu233.fml.util.Utils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class Transformer {
    private final boolean mappingOnly;
    private final HashMap<String, String> transformMap = new HashMap<>();
    private final HashMap<String, String> superClassMap = new HashMap<> ();
    private final HashMap<String, String> methodOwnerMap = new HashMap<>();
    private final ArrayListMultimap<String, FieldWrapper> fieldAddMap = ArrayListMultimap.create();
    private final ArrayListMultimap<String, MethodWrapper> methodAddMap = ArrayListMultimap.create();
    private final HashMap<String, String> linkFieldMap = new HashMap<>();
    private final HashMap<String,ClassNode> transformClassMap = new HashMap<>();

    public Transformer(boolean var1) {
        this.mappingOnly = var1;
    }


    public boolean hasBeenTransformed(String className){
        return this.transformMap.containsValue(className);
    }
    public byte[] transform(byte[] srcData) {
        srcData = this.mapping(srcData);
        if (this.mappingOnly) {
            return srcData;
        } else {
            final ClassReader srcReader = new ClassReader(srcData);
            ClassNode src = new ClassNode();
            srcReader.accept(src, 0);
            String loadingClassName = srcReader.getClassName();
            if (this.transformMap.containsKey(loadingClassName)) {
                //Interface transformer when load it
                this.interfaced(src);
                this.transformClassMap.put(loadingClassName, src);
            } else {
                //Transform original minecraft class
                String transformerName = "~NULL~";
                for (Map.Entry<String, String> transformerAndTargetClass : this.transformMap.entrySet()) {
                    if (transformerAndTargetClass.getValue().equals(loadingClassName)) {
                        transformerName = transformerAndTargetClass.getKey();
                        src.interfaces.add(transformerName);
                        ClassNode classNode = this.transformClassMap.get(transformerName);
                        if (classNode != null){
                            src.sourceFile = classNode.sourceFile;
                            src.interfaces.addAll(classNode.interfaces);
                        }
                    }
                }

                List<FieldWrapper> fieldWrappers = this.fieldAddMap.get(loadingClassName);
                String finalTransformerName = transformerName;
                fieldWrappers.removeIf(fieldWrapper-> {
                    for (FieldNode field : src.fields) {
                        if (field.name.equals(fieldWrapper.fieldName)){
                            if (fieldWrapper.isLinked){
                                return true;
                            }else {
                                System.err.println("[FML] WARNING : met same field name but didnt link:{ cl: " + loadingClassName + " | fd:" + fieldWrapper.fieldName + " | tr: "+ finalTransformerName +"}");
                                return false;
                            }
                        }
                    }
                    return false;
                });
                for (FieldWrapper fieldWrapper : fieldWrappers) {
                    src.fields.add(new FieldNode(fieldWrapper.opcode, fieldWrapper.fieldName, fieldWrapper.desc, fieldWrapper.signature, fieldWrapper.value));
                }

                //Transform methods
                List<MethodWrapper> var14 = this.methodAddMap.get(loadingClassName);

                for (MethodNode var16 : src.methods) {
                    MethodWrapper var17 = new MethodWrapper(var16.name, var16.desc, null);
//                    var16.instructions.iterator().forEachRemaining(abstractInsnNode -> {
//                        //LineNumberNode
//                        if (abstractInsnNode.getType() == 15){
//                            var16.instructions.remove(abstractInsnNode);
//                        }
//                    });
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
                    MethodNode methodNode = new MethodNode(var1x.node.access, var1x.methodName, var1x.desc, var1x.node.signature, var1x.node.exceptions.toArray(new String[0]));
                    methodNode.instructions.add(var1x.node.instructions);
                    methodNode.maxStack = var1x.node.maxStack;
                    methodNode.maxLocals = var1x.node.maxLocals;
                    methodNode.tryCatchBlocks = var1x.node.tryCatchBlocks;
                    methodNode.localVariables = var1x.node.localVariables;
                    methodNode.instructions.iterator().forEachRemaining(abstractInsnNode -> {
                        //LineNumberNode
                        if (abstractInsnNode.getType() == 15){
                            methodNode.instructions.remove(abstractInsnNode);
                        }
                    });
                    src.methods.add(methodNode);
                });
            }

            ClassWriter var13 = new ClassWriter(0);
            src.accept(new ClassVisitor(458752, var13) {
                public MethodVisitor visitMethod(int var1, String var2x, String var3, String var4, String[] var5) {
                    return new ModMethodVisitor(srcReader.getClassName(), var2x, var3, super.visitMethod(var1, var2x, var3, var4, var5));
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
        Iterator<MethodNode> var2 = var1.methods.iterator();
        while (var2.hasNext()) {
            var3 = var2.next();
            var3.instructions.clear();
            var3.access = 0x401;
            if (var3.name.equals("<init>")){
                var2.remove();
            }
        }

        var1.fields.clear();
        var1.access = 0x601;
        var1.superName = "java/lang/Object";

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
        for(Iterator<FieldNode> variterator = var2.fields.iterator(); variterator.hasNext(); var7.name = Mapping.getFieldMapName(var2.name + "." + var7.name)) {
            var7 = variterator.next();
        }

        ClassWriter var6 = new ClassWriter(0);
        var2.accept(new ClassVisitor(458752, var6) {
            public FieldVisitor visitField(int var1, String var2, String var3, String var4, Object var5) {
                return super.visitField(var1, var2, Transformer.this.replaceFieldDesc(var3), var4, var5);
            }

            public MethodVisitor visitMethod(int var1, String var2, String var3, String var4, String[] var5) {
                return new MappingMethodVisitor(super.visitMethod(var1, var2, Transformer.this.replaceMethodDesc(var3), var4, Transformer.this.transformInternalArray(var5)));
            }

            public void visit(int var1, int var2, String var3, String var4, String var5, String[] var6) {
                super.visit(var1, var2, var3, var4, Mapping.getClassMapName(var5.replace('/', '.')).replace('.', '/'), Transformer.this.transformInternalArray(var6));
            }
        });
        return var6.toByteArray();
    }

    private String[] transformInternalArray(String[] var1) {
        ArrayList<String> var2 = new ArrayList<>();

        for (String var6 : var1) {
            var2.add(Mapping.getClassMapName(var6.replace('/', '.')).replace('.', '/'));
        }

        return var2.toArray(new String[0]);
    }

    private String replaceMethodDesc(String var1) {
        String var2 = var1.substring(var1.indexOf(41) + 1);
        var2 = Transformer.this.replaceFieldDesc(var2);
        Type[] var3 = Type.getArgumentTypes(var1);
        StringBuilder var4 = new StringBuilder();

        for (Type var8 : var3) {
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

    public void addExtendsLink(String subClass,String superClass){
        this.superClassMap.put(subClass,superClass);
    }

    public void addMethodTransform(String var1, String var2, String var3, String var4, MethodNode var5) {
        this.addOrOverrideMethod(var1, var3, var4, var5);
    }

    private void addOrOverrideMethod(String var1, String var2, String var3, MethodNode var4) {
        this.methodAddMap.put(var1, new MethodWrapper(var2, var3, var4));
    }

    public void addFieldLink(String targetClass, String className, int access, String targetFieldName, String linkedName, String desc, String signature, Object value) {
        this.addField(targetClass, access, targetFieldName, linkedName, desc, signature, value,true);
        this.linkFieldMap.put(className + "/" + linkedName, targetFieldName);
    }



    public void addField(String targetClass,String className, int access, String targetFieldName, String linkedName, String desc, String signature, Object value){
        this.addField(targetClass, access, targetFieldName, linkedName, desc, signature, value,false);
    }
    private void addField(String targetClass, int access, String targetFieldName, String linkedName, String desc, String signature, Object value,boolean isLinked) {
        this.fieldAddMap.put(targetClass, new FieldWrapper(access, targetFieldName, linkedName, desc, signature, value,isLinked));
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

        public String getDesc() {
            return desc;
        }

        public String getMethodName() {
            return methodName;
        }


        public void visitFieldInsn(int opcode, String className, String srcFieldName, String descriptor) {
            if (Transformer.this.transformMap.containsKey(className)) {
                String targetName = Transformer.this.transformMap.get(className);
                if (targetName.equals(this.className)) {
                    String rawName = Transformer.this.linkFieldMap.get(className + "/" + srcFieldName);
                    if (rawName != null) {
                        super.visitFieldInsn(opcode, targetName, rawName, descriptor);
                    }else {
                        super.visitFieldInsn(opcode, targetName, srcFieldName, descriptor);
                    }
                } else {
                    String superClass = Transformer.this.superClassMap.get(className);
                    if (superClass != null){
                        super.visitFieldInsn(opcode, superClass, srcFieldName, descriptor);
                    }else {
                        super.visitFieldInsn(opcode, className, srcFieldName, descriptor);
                    }
                }
            } else {
                super.visitFieldInsn(opcode, className, srcFieldName, descriptor);
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
        public final boolean isLinked;

        public FieldWrapper(int var2, String var3, String var4, String var5, String var6, Object var7,boolean isLinked) {
            this.opcode = var2;
            this.fieldName = var3;
            this.linkedName = var4;
            this.desc = var5;
            this.signature = var6;
            this.value = var7;
            this.isLinked = isLinked;
        }
    }
}
