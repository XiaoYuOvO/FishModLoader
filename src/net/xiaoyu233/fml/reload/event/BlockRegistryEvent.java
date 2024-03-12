package net.xiaoyu233.fml.reload.event;

import net.minecraft.Block;
import net.xiaoyu233.fml.api.block.IBlock;

public class BlockRegistryEvent {
    public void registerBlock(Block block, String resourceLocation){
        block.setUnlocalizedName(resourceLocation);
        ((IBlock) block).setBlockTextureName(resourceLocation);
    }
}
