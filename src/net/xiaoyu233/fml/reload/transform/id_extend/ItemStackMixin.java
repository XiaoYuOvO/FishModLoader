package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin {
//    @Redirect(method = {"<init>(Lnet/minecraft/Block;I)V", "<init>(Lnet/minecraft/Block;II)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/Block;blockID:I"))
//    private static int redirectBlockId(Block block){
////        if (block.blockID > 256){
////            return 32256 - block.blockID; // (32000 - (blockId - 256))
////        }
//        return block.blockID;
//    }
}
