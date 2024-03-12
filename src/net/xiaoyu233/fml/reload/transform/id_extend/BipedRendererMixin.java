package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.RenderBiped;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RenderBiped.class)
public class BipedRendererMixin {
    @ModifyConstant(method = {
            "func_130005_c",
    }, constant = @Constant(intValue = 256))
    private static int modifyLimit(int value) {
        return 1024;
    }
}
