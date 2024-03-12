package net.xiaoyu233.fml.reload.event.recipe;

import net.minecraft.ItemStack;

public class RemoveRecipeModifier implements RecipeModifier{
    @Override
    public ItemStack getOutput() {
        return null;
    }

    @Override
    public Object[] toObjArgs() {
        return new Object[0];
    }

    @Override
    public RecipeType getType() {
        return RecipeType.REMOVE;
    }

    @Override
    public boolean isIncludeInLowestCraftingDifficultyDetermination() {
        return false;
    }
}
