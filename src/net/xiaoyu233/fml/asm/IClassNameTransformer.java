package net.xiaoyu233.fml.asm;

public interface IClassNameTransformer extends IClassTransformer {
   String unmapClassName(String var1);

   String remapClassName(String var1);
}
