package net.xiaoyu233.fml.reload.transform;

import net.minecraft.bjo;
import net.minecraft.bjp;
import net.minecraft.bke;
import net.xiaoyu233.fml.Translations;
import net.xiaoyu233.fml.reload.event.LanguageResourceReloadEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mixin(bke.class)
public class LanguageLoaderTrans {
   @Shadow
   Map a;

   @Overwrite
   public synchronized void a(bjp var1, List var2) {
      this.a.clear();

       for (Object o : var2) {
           String var4 = (String) o;
           String var5 = String.format("lang/%s.lang", var4);
           MITEEvents.MITE_EVENT_BUS.post(new LanguageResourceReloadEvent(this.a, var4));
           Translations.addTranslationsFor(this.a, var4);

           for (Object value : var1.a()) {
               String var7 = (String) value;

               try {
                   this.a(var1.b(new bjo(var7, var5)));
               } catch (IOException ignored) {
               }
           }
       }

      this.b();
   }

   @Shadow
   private void a(List b) throws IOException {
   }

   @Shadow
   private void b() {
   }
}
