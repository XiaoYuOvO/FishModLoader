package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.EnumCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EnumCommand.class)
public interface EnumCommandAccessor {

   @Invoker("get")
   static EnumCommand get(String text) {
      throw new AssertionError();
   }

    @Invoker("get")
    static EnumCommand get(int ordinal) {
       throw new AssertionError();
   }
}
