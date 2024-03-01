package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentWeaponDamage.class)
public class WeaponDamageTranslationFix extends Enchantment {
   protected WeaponDamageTranslationFix(int id, EnchantmentRarity rarity, int difficulty) {
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

   @Inject(method = "getTranslatedName", at = @At("HEAD"), cancellable = true)
   private void injectTranslateSlaying(Item item, CallbackInfoReturnable<String> callbackInfoReturnable) {
      if (this == Enchantment.sharpness && item instanceof ItemAxe) {
         callbackInfoReturnable.setReturnValue(LocaleI18n.translateToLocal("enchantment.slaying"));
      }
   }

   @Shadow
   public boolean isOnCreativeTab(CreativeModeTab creativeModeTab) {
      return false;
   }
}
