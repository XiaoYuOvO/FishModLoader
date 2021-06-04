package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnchantmentPiercing.class)
public abstract class EnchantmentPiercingTranslationFix extends Enchantment {

   protected EnchantmentPiercingTranslationFix(int id, yq rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   @Shadow
   public boolean canEnchantItem(Item var1) {
      return false;
   }


   @Shadow
   public String getNameSuffix() {
      return null;
   }

   @Overwrite
   public String getTranslatedName(Item item) {
      return item instanceof ItemAxe ? LocaleI18n.translateToLocal("enchantment.cleaving") : super.getTranslatedName(item);
   }

   @Shadow
   public boolean isOnCreativeTab(CreativeModeTab var1) {
      return false;
   }
}
