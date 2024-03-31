package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.ItemBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemBlock.class)
public class ItemBlockMixin {
//    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 256))
//    private static int injected(int value, Block block) {
////        if (block.blockID > 256){
////            //Extended block
////            //257 -> 31999
////            //blockId - 256 = delta
////            //32000 - (blockId - 256) = tobe
////            //32000 - (blockId - 256) - blockId = delta2
////            //256 + (blockId - here)
////            // -32000 - 257
////            //256 - delta2 = 256 - 32000 + (blockId - 256) + blockId
////            //290 -> 31966
////            //Original
//////            return 256 - (32000 - (block.blockID - 256) - block.blockID);
////            //Optimized
////            return -32000 + block.blockID * 2;
////        }
//        return 256;
//    }
}
