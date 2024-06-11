package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Item;
import net.xiaoyu233.fml.api.item.IItem;
import net.xiaoyu233.fml.util.WriteLockField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public abstract class ItemMixin implements IItem {
    private final WriteLockField<String> itemNamespace = WriteLockField.create("Minecraft");

    @Shadow protected abstract Item setTextureName(String par1Str);

    @Override
    public Item setItemTextureName(String par1Str){
        return this.setTextureName(par1Str);
    }


    @Override
    public String getTexturePrefix() {
        return "";
    }

    @Override
    public String getNamespace() {
        return itemNamespace.get();
    }

    @Override
    public void setNamespace(String blockNamespace) {
        this.itemNamespace.set(blockNamespace);
    }

    @Override
    public boolean hasNamespaceSet() {
        return itemNamespace.isLocked();
    }
}
