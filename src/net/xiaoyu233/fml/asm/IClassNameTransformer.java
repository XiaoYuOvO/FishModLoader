package net.xiaoyu233.fml.asm;

import org.spongepowered.asm.service.ILegacyClassTransformer;

public interface IClassNameTransformer extends IClassTransformer, ILegacyClassTransformer {
   String unmapClassName(String var1);

   String remapClassName(String var1);
}
