package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.Chunk;
import net.minecraft.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Chunk.class)
public class ChunkMixin {
    @Inject(method = "getBlockID", at = @At(value = "FIELD", target = "Lnet/minecraft/ExtendedBlockStorage;blockLSBArray:[B", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void injectUseExtBlockId(int par1, int par2, int par3, CallbackInfoReturnable<Integer> info, int par2_shifted, ExtendedBlockStorage extendedBlockStorage, int par2_and_15){
        info.setReturnValue(extendedBlockStorage.getExtBlockID(par1 & 15, par2_and_15, par3 & 15));
    }
}
