package net.xiaoyu233.fml.reload.transform.enum_extend;


import net.minecraft.EnumCreatureAttribute;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnumCreatureAttribute.class)
@SuppressWarnings("unused") // For mixin loader to trigger enum extender
public class EnumCreatureAttributeMixin {
}
