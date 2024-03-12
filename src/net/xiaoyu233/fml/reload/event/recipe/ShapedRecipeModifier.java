package net.xiaoyu233.fml.reload.event.recipe;

import net.minecraft.Item;
import net.minecraft.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedRecipeModifier implements RecipeModifier {
    private final ItemStack targetItem;
    private final List<String> pattern;
    private final Map<Character, ItemStack> ingredients;
    private final boolean include_in_lowest_crafting_difficulty_determination;

    public ShapedRecipeModifier(ItemStack targetItem, List<String> pattern, Map<Character, ItemStack> ingredients, boolean includeInLowestCraftingDifficultyDetermination) {
        this.targetItem = targetItem;
        this.pattern = pattern;
        this.ingredients = ingredients;
        include_in_lowest_crafting_difficulty_determination = includeInLowestCraftingDifficultyDetermination;
    }

    public ItemStack getOutput() {
        return targetItem;
    }

    public boolean isIncludeInLowestCraftingDifficultyDetermination() {
        return include_in_lowest_crafting_difficulty_determination;
    }

    public Object[] toObjArgs() {
        Object[] result = new Object[pattern.size() + ingredients.size() * 2];
        int currentIndex = 0;
        for (; currentIndex < pattern.size(); currentIndex++) {
            result[currentIndex] = pattern.get(currentIndex);
        }
        for (Map.Entry<Character, ItemStack> characterItemEntry : ingredients.entrySet()) {
            result[currentIndex++] = characterItemEntry.getKey();
            result[currentIndex++] = characterItemEntry.getValue();
        }
        return result;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }

    public static class Builder{
        private final ItemStack item;
        private final List<String> pattern = new ArrayList<>();
        private final Map<Character, ItemStack> ingredients = new HashMap<>();
        private boolean includeInLowestCraftingDifficultyDetermination = false;

        private Builder(ItemStack item) {
            this.item = item;
        }

        public static Builder of(ItemStack item){
            return new Builder(item);
        }

        public Builder pattern(String pattern){
            this.pattern.add(pattern);
            return this;
        }

        public Builder ingredient(char key, ItemStack ingredient){
            this.ingredients.put(key, ingredient);
            return this;
        }

        public Builder includeInLowestCraftingDifficultyDetermination(){
            includeInLowestCraftingDifficultyDetermination = true;
            return this;
        }

        public Builder ingredient(char key, Item ingredient){
            this.ingredients.put(key, new ItemStack(ingredient));
            return this;
        }

        public ShapedRecipeModifier build(){
            return new ShapedRecipeModifier(item, pattern, ingredients, includeInLowestCraftingDifficultyDetermination);
        }
    }
}
