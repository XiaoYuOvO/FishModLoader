package net.xiaoyu233.fml.reload.transform;

import net.minecraft.LocaleLanguage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin({LocaleLanguage.class})
public class LocaleLanguageTrans {
   @Shadow
   private static final LocaleLanguage instance = new LocaleLanguage();
   @Shadow
   private Map languageList;

   public Map getTranslationMap() {
      return this.languageList;
   }


   static {
      instance.getTranslationMap().put("enchantment.slaying", "杀害");
   }
}
