package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumItemInUseAction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumItemInUseAction.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumItemInUseActionMixin {
}
