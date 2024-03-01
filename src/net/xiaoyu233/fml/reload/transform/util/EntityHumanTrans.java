package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPlayer.class)
public interface EntityHumanTrans {
   @Invoker("getExperienceRequired")
   static int getExpRequired(int level) {
      throw new AssertionError();
   }
}
