package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Block.class)
public class BlockMixin {
    @ModifyConstant(method = {"<clinit>", "getBlock(Ljava/lang/String;)Lnet/minecraft/Block;",}, constant = @Constant(intValue = 256))
    private static int modifyMaxId(int value) {
        return 1024;
    }

}
