package net.xiaoyu233.fml;

import net.xiaoyu233.fml.config.InjectionConfig;

import javax.annotation.Nonnull;

public abstract class AbstractMod {
   public abstract void preInit();

   public void postInit() {
   }

   @Nonnull
   public abstract InjectionConfig getInjectionConfig();

   public abstract String modId();

   public abstract int modVerNum();

   public abstract String modVerStr();
}
