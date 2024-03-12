package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.RenderWitch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RenderWitch.class)
public class WitchRendererMixin {
    @ModifyConstant(method = {
            "a(Lnet/minecraft/EntityWitch;F)V",
    }, constant = @Constant(intValue = 256))
    private static int injected(int value) {
        return 1024;
    }
}
