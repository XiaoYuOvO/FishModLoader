package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.v;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Mixin( v.class)
public class AllowedCharFix {
    @Overwrite
   private static String getAllowedCharacters() {
      StringBuilder var0 = new StringBuilder();

      try {
         BufferedReader var1 = new BufferedReader(new InputStreamReader(AllowedCharFix.class.getResourceAsStream("/font.txt"), StandardCharsets.UTF_8));
         String var2 = "";

         while((var2 = var1.readLine()) != null) {
            if (!var2.startsWith("#")) {
               var0.append(var2);
            }
         }

         var1.close();
      } catch (Exception ignored) {
      }

      return var0.toString();
   }
}
