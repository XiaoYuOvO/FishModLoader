package net.xiaoyu233.fml.reload.transform;

import net.minecraft.Locale;
import net.minecraft.ResourceManager;
import net.xiaoyu233.fml.Translations;
import net.xiaoyu233.fml.reload.event.LanguageResourceReloadEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(Locale.class)
public class LanguageLoaderTrans {
   @Shadow
   Map field_135032_a;

   @Inject(method = "loadLocaleDataFiles", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
   public synchronized void a(ResourceManager var1, List var2, CallbackInfo callbackInfo, Iterator<?>iterator, String var4, String var5) {
//      this.a.clear();
//
//      for (Object o : var2) {
//           String var4 = (String) o;
//           String var5 = String.format("lang/%s.lang", var4);
           MITEEvents.MITE_EVENT_BUS.post(new LanguageResourceReloadEvent(this.field_135032_a, var4));
           Translations.addTranslationsFor(this.field_135032_a, var4);
//
//           for (Object value : var1.a()) {
//               String var7 = (String) value;
//
//               try {
//                   this.a(var1.b(new bjo(var7, var5)));
//               } catch (IOException ignored) {
//               }
//           }
//       }
//
//      this.b();
   }
}
