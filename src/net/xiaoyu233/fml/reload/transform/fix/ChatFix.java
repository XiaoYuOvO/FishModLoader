package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.Minecraft;
import net.minecraft.avk;
import net.minecraft.awe;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(awe.class)
public abstract class ChatFix extends avk {
   @Shadow()
   private Minecraft f;

   @Shadow
   public abstract void a(char var1, int var2);

   @Overwrite
   public void n() {
      try {
         int k = Keyboard.getEventKey();
         char c = Keyboard.getEventCharacter();
         if (Keyboard.getEventKeyState() || k == 0 && Character.isDefined(c)) {
            if (k == 87) {
               this.f.j();
               return;
            }

            this.a(c, k);
         }

      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }
}
