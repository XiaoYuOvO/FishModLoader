package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumMaterialHardness;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumMaterialHardness.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumMaterialHardnessMixin {
}
