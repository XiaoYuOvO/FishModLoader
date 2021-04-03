package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityPlayer.class)
public class FixBoundingBoxCheck {
   @Overwrite
   public void checkBoundingBoxAgainstSolidBlocks() {
   }
}
