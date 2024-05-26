package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumAction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumAction.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumActionMixin {
}
