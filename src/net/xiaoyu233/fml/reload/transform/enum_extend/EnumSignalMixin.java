package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumSignal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumSignal.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumSignalMixin {
}
