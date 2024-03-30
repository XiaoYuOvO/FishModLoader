package net.xiaoyu233.fml.reload.transform.fix.fix_recipe;

import net.minecraft.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Item.class)
public class ItemMixin {
    @ModifyConstant(method = {"<init>(ILjava/lang/String;I)V", "<init>()V"}, constant = @Constant(intValue = 65))
    private int injectExtendRecipeSize(int original) {
        return 1024;
    }
}