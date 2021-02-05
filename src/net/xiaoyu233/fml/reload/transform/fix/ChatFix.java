package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.Minecraft;
import net.minecraft.avk;
import net.minecraft.awe;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;
import org.lwjgl.input.Keyboard;

@Transform(awe.class)
public abstract class ChatFix extends avk {
   @Link("f")
   private Minecraft minecraft;

   public void n() {
      try {
         int k = Keyboard.getEventKey();
         char c = Keyboard.getEventCharacter();
         if (Keyboard.getEventKeyState() || k == 0 && Character.isDefined(c)) {
            if (k == 87) {
               this.minecraft.j();
               return;
            }

            this.a(c, k);
         }

      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   @Marker
   public abstract void a(char var1, int var2);
}
