package net.xiaoyu233.fml.reload.transform.fix.skin;

import net.minecraft.bfi;
import net.minecraft.bic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(bic.class)
public interface BicAccessor {
    @Accessor(value = "b")
    String getB();

    @Accessor(value = "c")
    bfi getC();
}
