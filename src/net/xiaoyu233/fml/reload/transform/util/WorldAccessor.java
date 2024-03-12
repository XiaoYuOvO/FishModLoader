package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.World;
import net.minecraft.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldAccessor {
   @Accessor
   WorldProvider getProvider();
}
