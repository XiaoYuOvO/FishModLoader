package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumStatus;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumStatus.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumStatusMixin {
}
