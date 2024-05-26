package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumTournamentType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumTournamentType.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumTournamentTypeMixin {
}
