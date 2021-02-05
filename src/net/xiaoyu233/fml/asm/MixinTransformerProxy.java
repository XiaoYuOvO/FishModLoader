package net.xiaoyu233.fml.asm;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.service.ILegacyClassTransformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MixinTransformerProxy implements IClassTransformer, ILegacyClassTransformer {
   private static final List<MixinTransformerProxy> proxies = new ArrayList();
   private static final MixinTransformer transformer = new MixinTransformer();
   private boolean isActive = true;

   public MixinTransformerProxy() {
      MixinTransformerProxy proxy;
      for(Iterator var1 = proxies.iterator(); var1.hasNext(); proxy.isActive = false) {
         proxy = (MixinTransformerProxy)var1.next();
      }

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
