package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumCommand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumCommand.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumCommandMixin {
}
