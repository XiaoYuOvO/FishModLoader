package net.xiaoyu233.fml.reload.event;

import net.minecraft.*;
import net.xiaoyu233.fml.api.block.IBlock;
import net.xiaoyu233.fml.api.item.IItem;

public class ItemRegistryEvent {
    public Item register(String resourceLocation, Item item, CreativeTabs tab) {
        ((IItem) item).setItemTextureName(((IItem) item).getTexturePrefix() + resourceLocation);
        item.setUnlocalizedName(resourceLocation);
        item.setCreativeTab(tab);
        return item;
    }

    public void registerAnvil(BlockAnvil block, String resourceLocation){
        block.setUnlocalizedName(resourceLocation);
        ((IBlock) block).setBlockTextureName(resourceLocation);
        Item item = new ItemAnvilBlock(block).setUnlocalizedName(resourceLocation);
        item.setMaxStackSize(block.getItemStackLimit());
    }
    public void registerItemBlock(Block block, String resourceLocation){
        block.setUnlocalizedName(resourceLocation);
        ((IBlock) block).setBlockTextureName(resourceLocation);
        Item item = new ItemBlock(block).setUnlocalizedName(resourceLocation);
        item.setMaxStackSize(block.getItemStackLimit());
    }

    public Item register(String resourceLocation, Item item) {
        ((IItem) item).setItemTextureName(((IItem) item).getTexturePrefix() + resourceLocation);
        item.setUnlocalizedName(resourceLocation);
        return item;
    }
}
