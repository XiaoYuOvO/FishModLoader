package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumEntityState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumEntityState.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumEntityStateMixin {
}
