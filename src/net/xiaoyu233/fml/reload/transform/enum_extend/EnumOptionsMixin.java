package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumOptions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumOptions.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumOptionsMixin {
}
