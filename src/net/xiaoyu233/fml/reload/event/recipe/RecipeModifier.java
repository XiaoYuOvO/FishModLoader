package net.xiaoyu233.fml.reload.event.recipe;

import net.minecraft.ItemStack;

public interface RecipeModifier {
    ItemStack getOutput();

    Object[] toObjArgs();
    RecipeType getType();
    boolean isIncludeInLowestCraftingDifficultyDetermination();
}
