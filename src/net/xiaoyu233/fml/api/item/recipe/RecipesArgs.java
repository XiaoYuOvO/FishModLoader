package net.xiaoyu233.fml.api.item.recipe;

import net.minecraft.ItemStack;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class RecipesArgs {
    public final ItemStack result;
    public final Object[] inputs;
    public final boolean include_in_lowest_crafting_difficulty_determination;
    private boolean extendsNBT;
    private Optional<Float> difficulty = Optional.empty();
    public RecipesArgs(ItemStack result, boolean include_in_lowest_crafting_difficulty_determination, Object... inputs){
        this.result = result;
        this.include_in_lowest_crafting_difficulty_determination = include_in_lowest_crafting_difficulty_determination;
        this.inputs = inputs;
    }

    public boolean isExtendsNBT() {
        return extendsNBT;
    }

    public Optional<Float> getDifficulty() {
        return difficulty;
    }

    public RecipesArgs difficulty(float difficulty) {
        this.difficulty = Optional.of(difficulty);
        return this;
    }

    public RecipesArgs extendsNBT() {
        this.extendsNBT = true;
        return this;
    }
}
