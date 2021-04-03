package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnchantmentWeaponDamage.class)
public class WeaponDamageTranslationFix extends Enchantment {
   protected WeaponDamageTranslationFix(int id, yq rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   @Shadow
   public boolean canEnchantItem(Item item) {
      return false;
   }

   @Shadow
   public String getNameSuffix() {
      return null;
   }

   @Overwrite
   public String getTranslatedName(Item item) {
      return this == Enchantment.sharpness && item instanceof ItemAxe ? LocaleI18n.translateToLocal("enchantment.slaying") : super.getTranslatedName(item);
   }

   @Shadow
   public boolean isOnCreativeTab(CreativeModeTab creativeModeTab) {
      return false;
   }
}
