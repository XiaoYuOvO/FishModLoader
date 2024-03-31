package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.Item;
import net.minecraft.ItemBlock;
import net.minecraft.RenderSnowMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderSnowMan.class)
public class SnowManRendererMixin {
    @Redirect(method = {
            "renderSnowmanPumpkin(Lnet/minecraft/EntitySnowman;F)V",
    }, at = @At(value = "FIELD", target = "Lnet/minecraft/Item;itemID:I"))
    private int injected(Item item) {
        if (item instanceof ItemBlock) return 0;
        return item.itemID;
    }
}
