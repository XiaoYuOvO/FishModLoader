package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityItem.class)
public class EntityItemMixin {
    @ModifyConstant(method = {
            "isImmuneToExplosion",
            "handleExplosion",
    }, constant = @Constant(intValue = 256))
    private static int injected(int value) {
        return 1024;
    }
}
