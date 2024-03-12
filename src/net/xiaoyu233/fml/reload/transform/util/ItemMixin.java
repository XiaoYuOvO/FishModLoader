package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Item;
import net.xiaoyu233.fml.api.item.IItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public abstract class ItemMixin implements IItem {

    @Shadow public static Item redstone;

    @Shadow protected abstract Item setTextureName(String par1Str);

    @Override
    public Item setItemTextureName(String par1Str){
        return this.setTextureName(par1Str);
    }


    @Override
    public String getTexturePrefix() {
        return "";
    }
}
