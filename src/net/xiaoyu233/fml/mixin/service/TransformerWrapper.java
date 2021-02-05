package net.xiaoyu233.fml.mixin.service;

import net.xiaoyu233.fml.asm.IClassTransformer;
import org.spongepowered.asm.service.ILegacyClassTransformer;

import javax.annotation.Resource;

public class TransformerWrapper implements ILegacyClassTransformer {
   private final IClassTransformer transformer;

   TransformerWrapper(IClassTransformer transformer) {
      this.transformer = transformer;
   }

   public String getName() {
      return this.transformer.getClass().getName();
   }

   public boolean isDelegationExcluded() {
      return this.transformer.getClass().getAnnotation(Resource.class) != null;
   }

   public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
      return this.transformer.transform(name, transformedName, basicClass);
   }
}
