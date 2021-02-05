package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.EnumCommand;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(EnumCommand.class)
public class EnumCommandTrans {
   @Marker
   static EnumCommand get(int ordinal) {
      return null;
   }

   @Marker
   static EnumCommand get(String text) {
      return null;
   }

   public static EnumCommand get0(String text) {
      return get(text);
   }

   public static EnumCommand get0(int ordinal) {
      return get(ordinal);
   }
}
