package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumMobType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumMobType.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumMobTypeMixin {
}
