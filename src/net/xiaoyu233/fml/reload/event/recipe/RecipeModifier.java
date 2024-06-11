package net.xiaoyu233.fml.reload.event.recipe;

import net.minecraft.ItemStack;

import java.util.Optional;

public interface RecipeModifier {
    ItemStack getOutput();

    Object[] toObjArgs();
    RecipeType getType();
    boolean isIncludeInLowestCraftingDifficultyDetermination();

    Optional<Float> getCraftingDifficulty();
}
