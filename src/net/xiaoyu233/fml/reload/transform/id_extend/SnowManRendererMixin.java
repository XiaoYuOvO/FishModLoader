package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.RenderSnowMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RenderSnowMan.class)
public class SnowManRendererMixin {
    @ModifyConstant(method = {
            "renderSnowmanPumpkin(Lnet/minecraft/EntitySnowman;F)V",
    }, constant = @Constant(intValue = 256))
    private static int injected(int value) {
        return 1024;
    }
}
