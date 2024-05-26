package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumBlockFX;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumBlockFX.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumBlockFXMixin {
}
