package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.Chunk;
import net.minecraft.ExtendedBlockStorage;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public class WorldMixin {
    @Inject(locals = LocalCapture.CAPTURE_FAILHARD, method = "getBlockId", at = @At(value = "FIELD", target = "Lnet/minecraft/Chunk;storageArrays:[Lnet/minecraft/ExtendedBlockStorage;", shift = At.Shift.AFTER), cancellable = true)
    private void injectGetBlockId(int par1, int par2, int par3, CallbackInfoReturnable<Integer> cir, Chunk var4) {
        ExtendedBlockStorage extended_block_storage = var4.storageArrays[par2 >> 4];
        if (extended_block_storage == null) {
            cir.setReturnValue(0);
        } else {
            int par1_and_15 = par1 & 15;
            int par2_and_15 = par2 & 15;
            int par3_and_15 = par3 & 15;
            cir.setReturnValue(extended_block_storage.getExtBlockID(par1_and_15, par2_and_15, par3_and_15));
        }
    }
}
