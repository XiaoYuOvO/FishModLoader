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

   public static void addTranslation(String key, String value) {
   }

   public Map getTranslationMap() {
      return this.languageList;
   }

   static {
      addTranslation("enchantment.slaying", "\u6740\u5bb3");
   }
}
