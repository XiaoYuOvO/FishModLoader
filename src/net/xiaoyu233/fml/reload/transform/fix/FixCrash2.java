package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.bbs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(bbs.class)
public class FixCrash2 {
   @Overwrite
   private static void SysX() {
   }

   @Overwrite
   private static boolean isRbf() {
      return true;
   }

   @Overwrite
   private static void method2() {
   }
}
