package net.xiaoyu233.fml.asm;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.service.ILegacyClassTransformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MixinTransformerProxy implements IClassTransformer, ILegacyClassTransformer {
   private static final List<MixinTransformerProxy> proxies = new ArrayList<>();
   private final ILegacyClassTransformer transformer;
   private boolean isActive = true;

   public MixinTransformerProxy(ILegacyClassTransformer transformer) {
      MixinTransformerProxy proxy;
      for(Iterator<MixinTransformerProxy> var1 = proxies.iterator(); var1.hasNext(); proxy.isActive = false) {
         proxy = var1.next();
      }
      this.transformer = transformer;

      proxies.add(this);
      LogManager.getLogger("mixin").debug("Adding new mixin transformer proxy #{}", proxies.size());
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      return this.isActive ? transformer.transformClassBytes(name, transformedName, basicClass) : basicClass;
   }

   public String getName() {
      return this.getClass().getName();
   }

   public boolean isDelegationExcluded() {
      return true;
   }

   public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
      return this.isActive ? transformer.transformClassBytes(name, transformedName, basicClass) : basicClass;
   }
}
