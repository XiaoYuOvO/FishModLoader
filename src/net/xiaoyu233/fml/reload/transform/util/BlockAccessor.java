package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Block;
import net.minecraft.StepSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {
    @Invoker("setHardness")
    Block setBlockHardness(float resistance);
    @Invoker("setResistance")
    Block setBlockResistance(float v);
    @Invoker("setLightValue")
    Block setBlockLightValue(float v);
    @Invoker("setTextureName")
    Block setBlockTextureName(String location);
    @Invoker("setStepSound")
    Block setBlockStepSound(StepSound stepSound);
}
