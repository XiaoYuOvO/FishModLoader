package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.Block;
import net.minecraft.BlockMinecartTrack;
import net.minecraft.BlockRedstoneTorch;
import net.minecraft.BlockTorch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockRedstoneTorch.class)
public abstract class RedstoneTorchFix extends BlockTorch {
   protected RedstoneTorchFix(int par1, boolean par2) {
      super(par1);
   }

   @Overwrite
   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return !(other_block instanceof BlockRedstoneTorch) && !(other_block instanceof BlockMinecartTrack) && super.canBeReplacedBy(metadata, other_block, other_block_metadata);
   }
}
