package net.xiaoyu233.fml;

import net.xiaoyu233.fml.config.ConfigRegistry;
import net.xiaoyu233.fml.config.InjectionConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractMod {
   /**
    * This method will be called when the mod loader loaded all the mods into the mod list
    * and it will initialize the mod class instance AGAIN with the LaunchClassLoader,
    * so it means you can access minecraft in it now
    **/
   public void postInit() {
   }

   /**
    * This method will be called when the mod loader find the mod and it will initialize the mod class instance with
    * the URLClassLoader,so it means you cannot access any minecraft classes in this method or you will have a lot of
    * CRASH!
   **/
   public abstract void preInit();

   @Nonnull
   public abstract InjectionConfig getInjectionConfig();

   public abstract String modId();

   public abstract int modVerNum();

   public abstract String modVerStr();

   @Nullable
   public ConfigRegistry getConfigRegistry() {
       return null;
   }
}
