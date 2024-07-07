package net.xiaoyu233.fml.reload.transform.registry;

import net.minecraft.*;
import net.xiaoyu233.fml.api.item.recipe.RecipesArgs;
import net.xiaoyu233.fml.reload.event.*;
import net.xiaoyu233.fml.reload.event.recipe.RecipeModifier;
import net.xiaoyu233.fml.reload.transform.util.CraftingManagerInvoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mixin(CraftingManager.class)
public abstract class ItemBlockRecipeRegistryMixin {
    @Shadow public abstract ShapelessRecipes addShapelessRecipe(ItemStack par1ItemStack, boolean include_in_lowest_crafting_difficulty_determination, Object... par2ArrayOfObj);

    @Shadow public abstract ShapedRecipes addRecipe(ItemStack par1ItemStack, boolean include_in_lowest_crafting_difficulty_determination, Object... par2ArrayOfObj);

    @Redirect(method = "<init>",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/RecipesMITE;addCraftingRecipes(Lnet/minecraft/CraftingManager;)V"))
    private void injectRegisterRecipes(CraftingManager crafters) {
        MITEEvents.MITE_EVENT_BUS.post(new ItemRegistryEvent());
        MITEEvents.MITE_EVENT_BUS.post(new BlockRegistryEvent());
        MITEEvents.MITE_EVENT_BUS.post(new AchievementRegistryEvent());
        RecipeRegistryEvent event = new RecipeRegistryEvent();
        MITEEvents.MITE_EVENT_BUS.post(event);
        RecipesMITE.addCraftingRecipes(crafters);
        for (RecipesArgs args : event.getShapedRecipes()) {
            ShapedRecipes shapedRecipes = ((CraftingManagerInvoker) crafters).addRecipe(args.result, args.include_in_lowest_crafting_difficulty_determination, args.inputs);
            args.getDifficulty().ifPresent(shapedRecipes::setDifficulty);
            if (args.isExtendsNBT()){
                shapedRecipes.func_92100_c();
            }
        }
        for (RecipesArgs args : event.getShapelessRecipe()) {
            ShapelessRecipes shapelessRecipes = ((CraftingManagerInvoker) crafters).addShapelessRecipe(args.result, args.include_in_lowest_crafting_difficulty_determination, args.inputs);
            args.getDifficulty().ifPresent(shapelessRecipes::setDifficulty);
            if (args.isExtendsNBT()){
                shapelessRecipes.propagateTagCompound();
            }
        }
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;Ljava/util/Comparator;)V"))
    private void injectApplyModifier(List<IRecipe> list, Comparator<IRecipe> comparator){
        RecipeModifyEvent event = new RecipeModifyEvent();
        MITEEvents.MITE_EVENT_BUS.post(event);
        for (IRecipe iRecipe : list) {
            ItemStack recipeOutput = iRecipe.getRecipeOutput();
            if (recipeOutput == null) continue;
            RecipeModifier recipeModifier = event.getModifiers().get(new RecipeModifyEvent.ItemInfo(recipeOutput.getItem(), recipeOutput.stackSize, recipeOutput.getItemSubtype()));
            if (recipeModifier != null){
                list.remove(iRecipe);
                switch (recipeModifier.getType()){
                    case SHAPED:
                        ShapedRecipes shapedRecipes = addRecipe(recipeModifier.getOutput(), recipeModifier.isIncludeInLowestCraftingDifficultyDetermination(), recipeModifier.toObjArgs());
                        recipeModifier.getCraftingDifficulty().ifPresent(shapedRecipes::setDifficulty);
                        break;
                    case SHAPELESS:
                        ShapelessRecipes shapelessRecipes = addShapelessRecipe(recipeModifier.getOutput(), recipeModifier.isIncludeInLowestCraftingDifficultyDetermination(), recipeModifier.toObjArgs());
                        recipeModifier.getCraftingDifficulty().ifPresent(shapelessRecipes::setDifficulty);
                        break;
                    case REMOVE:
                        break;
                }
            }
        }
        Collections.sort(list, comparator);
    }


}
