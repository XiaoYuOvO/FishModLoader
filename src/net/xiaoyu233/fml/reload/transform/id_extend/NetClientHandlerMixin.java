package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.NetClientHandler;
import net.minecraft.Packet97MultiBlockChange;
import net.minecraft.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
    @Shadow private WorldClient worldClient;

    @ModifyConstant(method = "handleMultiBlockChange(Lnet/minecraft/Packet97MultiBlockChange;)V", constant = @Constant(intValue = 255, ordinal = 1))
    private int modifyBlockIdLimit(int original){
        //Invalid AND operator
        return 0b11111111111111111111111111111111;
    }
    @ModifyConstant(method = "handleMultiBlockChange(Lnet/minecraft/Packet52MultiBlockChange;)V", constant = @Constant(intValue = 4095, ordinal = 0))
    private int modifyBlockIdLimit2(int original){
        //Invalid AND operator
        return 0b11111111111111111111111111111111;
    }
    @ModifyConstant(method = "handleMultiBlockChange(Lnet/minecraft/Packet52MultiBlockChange;)V", constant = @Constant(intValue = 4))
    private int modifyBlockIdLimit3(int original){
        return 8;
    }
    @ModifyConstant(method = "handleMultiBlockChange(Lnet/minecraft/Packet97MultiBlockChange;)V", constant = @Constant(intValue = 4, ordinal = 0))
    private int modifyMetadataOffset(int original){
        return 5;
    }

    @ModifyConstant(method = "handleMultiBlockChange(Lnet/minecraft/Packet97MultiBlockChange;)V", constant = @Constant(intValue = 5, ordinal = 0))
    private int modifyBlockInterval(int original){
        //Add one byte for a block
        return 6;
    }

    @Redirect(method = "handleMultiBlockChange(Lnet/minecraft/Packet97MultiBlockChange;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/WorldClient;setBlockAndMetadataAndInvalidate(IIIII)Z"))
    private boolean cancelOldSetBlock(WorldClient client, int par1, int par2, int par3, int par4, int par5, Packet97MultiBlockChange packet97MultiBlockChange){
        return false;
    }

    @Inject(method = "handleMultiBlockChange(Lnet/minecraft/Packet97MultiBlockChange;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/WorldClient;setBlockAndMetadataAndInvalidate(IIIII)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void newSetBlock(Packet97MultiBlockChange packet97MultiBlockChange, CallbackInfo callbackInfo, byte[] bytes, int base_x, int base_z, long before, int delay, int offset, int x, int y, int z, int block_id, int metadata){
        int id_extra = bytes[offset + 4];
        if(id_extra < 0) {
            id_extra = 256 + id_extra;
        }
        this.worldClient.setBlockAndMetadataAndInvalidate(x, y, z, block_id * 256 + id_extra, metadata);
    }

    @ModifyConstant(method = "handleBlockFX", constant = @Constant(intValue = 0xFF))
    private int invalidateBlockFXAndOP(int original){
        //We now support 12bits!!!!
        return 0b111111111111; // 4095
    }

    @ModifyConstant(method = "handleBlockFX", constant = @Constant(intValue = 8), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/EnumBlockFX;destroy:Lnet/minecraft/EnumBlockFX;")))
    private int moveBlockFXOriginalBlockReadPtr(int original){
        return 12;
    }

    @ModifyConstant(method = "handleBlockFX", constant = @Constant(intValue = 12), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/EnumBlockFX;destroy:Lnet/minecraft/EnumBlockFX;")))
    private int moveBlockFXSuccessorBlockReadPtr(int original){
        return 16;
    }

    @ModifyConstant(method = "handleBlockFX", constant = @Constant(intValue = 20), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/EnumBlockFX;destroy:Lnet/minecraft/EnumBlockFX;")))
    private int moveBlockFXSuccessorMetaReadPtr(int original){
        return 28;
    }

}
