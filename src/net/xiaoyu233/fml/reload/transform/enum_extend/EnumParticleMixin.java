package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumParticle;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumParticle.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumParticleMixin {
}
