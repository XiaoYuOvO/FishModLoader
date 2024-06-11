package net.xiaoyu233.fml.reload.event;

import net.minecraft.*;
import net.xiaoyu233.fml.api.block.IBlock;
import net.xiaoyu233.fml.api.item.IItem;

public class ItemRegistryEvent {
    public Item register(String namespace, String resourceLocation, Item item, CreativeTabs tab) {
        ((IItem) item).setItemTextureName(((IItem) item).getTexturePrefix() + resourceLocation);
        item.setUnlocalizedName(resourceLocation);
        item.setNamespace(namespace);
        item.setCreativeTab(tab);
        return item;
    }

    public void registerAnvil(String namespace, String resourceLocation, BlockAnvil block){
        block.setUnlocalizedName(resourceLocation);
        if (!block.hasNamespaceSet()) {
            block.setNamespace(namespace);
        }
        ((IBlock) block).setBlockTextureName(resourceLocation);
        Item item = new ItemAnvilBlock(block).setUnlocalizedName(resourceLocation);
        item.setNamespace(namespace);
        item.setMaxStackSize(block.getItemStackLimit());
    }
    public void registerItemBlock(String namespace, String resourceLocation, Block block){
        block.setUnlocalizedName(resourceLocation);
        if (!block.hasNamespaceSet()) {
            block.setNamespace(namespace);
        }
        ((IBlock) block).setBlockTextureName(resourceLocation);
        Item item = new ItemBlock(block).setUnlocalizedName(resourceLocation);
        item.setNamespace(namespace);
        item.setMaxStackSize(block.getItemStackLimit());
    }

    public Item register(String namespace, String resourceLocation, Item item) {
        ((IItem) item).setItemTextureName(((IItem) item).getTexturePrefix() + resourceLocation);
        item.setNamespace(namespace);
        item.setUnlocalizedName(resourceLocation);
        return item;
    }
}
