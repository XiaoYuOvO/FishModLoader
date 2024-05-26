package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumSide;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumSide.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumSideMixin {
}
