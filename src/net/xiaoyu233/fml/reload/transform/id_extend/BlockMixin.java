package net.xiaoyu233.fml.reload.transform.id_extend;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class BlockMixin {
    @Shadow @Final public int blockID;

    @ModifyConstant(method = {"<clinit>", "getBlock(Ljava/lang/String;)Lnet/minecraft/Block;",}, constant = @Constant(intValue = 256))
    private static int modifyMaxId(int value) {
        return 32000;
    }

    @ModifyConstant(method = {"<clinit>", "getBlock(Ljava/lang/String;)Lnet/minecraft/Block;",}, constant = @Constant(intValue = 4096))
    private static int modifyMaxId2(int value) {
        return 32000;
    }

    @Redirect(method = "tryPlaceBlock(Lnet/minecraft/World;IIILnet/minecraft/EnumFace;ILnet/minecraft/Entity;ZZZ)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/SignalData;setInteger(I)Lnet/minecraft/SignalData;"))
    private SignalData redirectSetBreakSignalData(SignalData data, int value, World world, int x, int y, int z, EnumFace face, int metadata, Entity placer, boolean perform_placement_check, boolean drop_existing_block, boolean test_only, @Local Block existing_block, @Local BlockBreakInfo info){
        return data.setInteger(existing_block.blockID + (info.getMetadata() << 12) + (this.blockID << 16) + (metadata << 28));
    }
}
