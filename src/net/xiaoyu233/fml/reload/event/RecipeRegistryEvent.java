package net.xiaoyu233.fml.reload.event;

import net.minecraft.ItemStack;
import net.xiaoyu233.fml.api.item.recipe.RecipesArgs;

import java.util.ArrayList;
import java.util.List;

public class RecipeRegistryEvent {

    private final List<RecipesArgs> shapedRecipes = new ArrayList<>();
    private final List<RecipesArgs> shapelessRecipe = new ArrayList<>();
    public RecipeRegistryEvent() {}

    public List<RecipesArgs> getShapedRecipes() {
        return shapedRecipes;
    }

    public List<RecipesArgs> getShapelessRecipe() {
        return shapelessRecipe;
    }

    public RecipesArgs registerShapedRecipe(ItemStack out, boolean include_in_lowest_crafting_difficulty_determination, Object... input){
        RecipesArgs e = new RecipesArgs(out, include_in_lowest_crafting_difficulty_determination, input);
        this.shapedRecipes.add(e);
        return e;
    }

    public RecipesArgs registerShapelessRecipe(ItemStack out, boolean include_in_lowest_crafting_difficulty_determination, Object... input){
        RecipesArgs e = new RecipesArgs(out, include_in_lowest_crafting_difficulty_determination, input);
        this.shapelessRecipe.add(e);
        return e;
    }
}
