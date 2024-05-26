package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumCreatureType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumCreatureType.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumCreatureTypeMixin {
}
