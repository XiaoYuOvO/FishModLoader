package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.AnvilChunkLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilChunkLoader.class)
public class ChunkRegionLoaderMixin {
    @ModifyConstant(method = "getInvalidSectionBlockConversionIdsOrMetadata", constant = @Constant(intValue = 256))
    private static int injectedModifyLength(int original) {
        return 1024;
    }

//    @ModifyExpressionValue(method = "handleSectionChecksumFailure", at = @At(value = "", opcode = Opcodes.IINC))
//    private int injected(int value) {
//        return 1024;
//    }
}
