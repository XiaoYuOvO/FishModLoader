package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.*;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(BlockAnvil.class)
public class FixAnvilStackCrash extends BlockFalling {
    @Marker
    public FixAnvilStackCrash(int par1, Material material, BlockConstants constants) {
        super(par1, material, constants);
    }

    public boolean canFallDownTo(World world, int x, int y, int z, int metadata) {
        Block block_below = world.getBlock(x, y, z);
        int block_below_metadata = world.h(x, y, z);
        return (block_below == null || !block_below.isSolid(block_below_metadata) || EntityFallingBlock.canDislodgeOrCrushBlockAt(world, this, metadata, x, y, z)) && !(world.getBlock(x, y, z) instanceof BlockLadder);
    }
}
