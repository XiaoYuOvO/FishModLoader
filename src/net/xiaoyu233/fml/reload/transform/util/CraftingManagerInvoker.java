package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.CraftingManager;
import net.minecraft.ItemStack;
import net.minecraft.ShapedRecipes;
import net.minecraft.ShapelessRecipes;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CraftingManager.class)
public interface CraftingManagerInvoker {
    @Invoker("addRecipe")
    @Intrinsic
    ShapedRecipes addRecipe(ItemStack par1ItemStack, boolean include_in_lowest_crafting_difficulty_determination, Object... par2ArrayOfObj);
    @Invoker("addShapelessRecipe")
    @Intrinsic
    ShapelessRecipes addShapelessRecipe(ItemStack par1ItemStack, boolean include_in_lowest_crafting_difficulty_determination, Object... par2ArrayOfObj);
}
