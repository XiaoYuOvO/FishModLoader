package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.EnumCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EnumCommand.class)
public class EnumCommandTrans {
   @Shadow
   private static EnumCommand get(int ordinal) {
      return null;
   }

   @Shadow
   private static EnumCommand get(String text) {
      return null;
   }

   @Invoker("get")
   public static EnumCommand get0(String text) {
      return get(text);
   }

    @Invoker("get")
   public static EnumCommand get0(int ordinal) {
      return get(ordinal);
   }
}
