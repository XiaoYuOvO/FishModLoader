package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExtendedBlockStorage.class)
public class ChunkSectionMixin {
    @Redirect(method = {
            "setExtBlockID",
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/Debug;setErrorMessage(Ljava/lang/String;)V"))
    public void deletePrintBlockIdMessage(String message) {
    }
}
