package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Block;
import net.minecraft.CreativeTabs;
import net.minecraft.StepSound;
import net.xiaoyu233.fml.api.block.IBlock;
import net.xiaoyu233.fml.util.WriteLockField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin implements IBlock {
    private WriteLockField<String> blockNamespace = WriteLockField.create("Minecraft");
    @Shadow
    public Block setTextureName(String location) {
        return null;
    }

    @Shadow
    public Block setHardness(float v) {
        return null;
    }

    @Shadow
    public Block setResistance(float par1) {
        return null;
    }

    @Shadow
    public Block setLightValue(float exp) {
        return null;
    }

    @Shadow
    public Block setStepSound(StepSound par1StepSound) {
        return null;
    }

    @Override
    public IBlock setBlockTextureName(String location) {
        return (IBlock) setTextureName(location);
    }

    @Override
    public IBlock setBlockHardness(float v) {
        return (IBlock) setHardness(v);
    }

    @Override
    public IBlock setBlockResistance(float par1) {
        return (IBlock) setResistance(par1);
    }

    @Override
    public IBlock setBlockLightValue(float exp) {
        return (IBlock) setLightValue(exp);
    }

    @Override
    public IBlock setBlockStepSound(StepSound par1StepSound) {
        return (IBlock) setStepSound(par1StepSound);
    }

    @Shadow
    public Block setMaxStackSize(int size) {
        return null;
    }

    @Shadow public abstract Block setCreativeTab(CreativeTabs par1CreativeTabs);

    @Shadow public abstract Block setUnlocalizedName(String par1Str);

    @Override
    public IBlock setBlockMaxStackSize(int size) {
        return (IBlock) setMaxStackSize(size);
    }

    @Override
    public IBlock setBlockCreativeTab(CreativeTabs tab) {
        return (IBlock) setCreativeTab(tab);
    }

    @Override
    public IBlock setBlockUnlocalizedName(String name) {
        return (IBlock) setUnlocalizedName(name);
    }

    @Override
    public String getNamespace() {
        return blockNamespace.get();
    }

    @Override
    public void setNamespace(String blockNamespace) {
        this.blockNamespace.set(blockNamespace);
    }

    @Override
    public boolean hasNamespaceSet() {
        return blockNamespace.isLocked();
    }
}
