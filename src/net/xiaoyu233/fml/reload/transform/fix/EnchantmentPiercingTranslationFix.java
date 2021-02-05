package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.*;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(EnchantmentPiercing.class)
public class EnchantmentPiercingTranslationFix extends Enchantment {
   @Marker
   protected EnchantmentPiercingTranslationFix(int id, yq rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   @Marker
   public String getNameSuffix() {
      return null;
   }

   public String getTranslatedName(Item item) {
      return item instanceof ItemAxe ? LocaleI18n.a("enchantment.cleaving") : super.getTranslatedName(item);
   }

   @Marker
   public boolean canEnchantItem(Item var1) {
      return false;
   }

   @Marker
   public boolean isOnCreativeTab(CreativeModeTab var1) {
      return false;
   }
}
