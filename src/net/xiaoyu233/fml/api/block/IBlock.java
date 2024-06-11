package net.xiaoyu233.fml.api.block;

import net.minecraft.CreativeTabs;
import net.minecraft.StepSound;
import net.xiaoyu233.fml.api.INamespaced;

public interface IBlock extends INamespaced {
    default IBlock setBlockTextureName(String location){throw new AssertionError();};
    default IBlock setBlockHardness(float v){throw new AssertionError();};
    default IBlock setBlockResistance(float par1){throw new AssertionError();};
    default IBlock setBlockLightValue(float exp){throw new AssertionError();};
    default IBlock setBlockStepSound(StepSound par1StepSound){throw new AssertionError();};
    default IBlock setBlockMaxStackSize(int size){throw new AssertionError();};
    default IBlock setBlockCreativeTab(CreativeTabs tab){throw new AssertionError();};
    default IBlock setBlockUnlocalizedName(String name){throw new AssertionError();};
}
