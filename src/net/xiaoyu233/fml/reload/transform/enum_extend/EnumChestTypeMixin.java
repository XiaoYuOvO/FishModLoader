package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumChestType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumChestType.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumChestTypeMixin {
}
