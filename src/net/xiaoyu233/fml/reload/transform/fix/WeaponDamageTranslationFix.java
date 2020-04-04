package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.*;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(EnchantmentWeaponDamage.class)
public class WeaponDamageTranslationFix extends Enchantment {
    @Marker
    protected WeaponDamageTranslationFix(int id, yq rarity, int difficulty) {
        super(id, rarity, difficulty);
    }

    @Override
    @Marker
    public String getNameSuffix() {
        return null;
    }

    @Override
    @Marker
    public boolean canEnchantItem(Item item) {
        return false;
    }

    public String getTranslatedName(Item item) {
        return this == Enchantment.l && item instanceof ItemAxe ? LocaleI18n.a("enchantment.slaying") : super.getTranslatedName(item);
    }

    @Override
    @Marker
    public boolean isOnCreativeTab(CreativeModeTab creativeModeTab) {
        return false;
    }
}
