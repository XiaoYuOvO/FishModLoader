package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.EntityEnderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityEnderman.class)
public class EndermanMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 256))
    private static int modifyCarriableBlockCount(int val){
        return 4096;
    }
}
