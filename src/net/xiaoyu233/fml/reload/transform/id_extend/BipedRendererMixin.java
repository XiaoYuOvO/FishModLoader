package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.Item;
import net.minecraft.ItemBlock;
import net.minecraft.ItemStack;
import net.minecraft.RenderBiped;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderBiped.class)
public class BipedRendererMixin {
    @Redirect(method = "func_130005_c", at = @At(value = "FIELD", target = "Lnet/minecraft/ItemStack;itemID:I", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/TileEntitySkullRenderer;func_82393_a(FFFIFILjava/lang/String;)V"),to = @At(value = "CONSTANT", args = "classValue=net/minecraft/ItemBow")))
    private int redirectGetItemId(ItemStack stack){
        if (stack.getItem() instanceof ItemBlock) return 0;
        return stack.itemID;
    }

    @Redirect(method = "func_130005_c", at = @At(value = "FIELD", target = "Lnet/minecraft/Item;itemID:I"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/RenderBlocks;renderItemIn3d(I)Z", ordinal = 0)))
    private int redirectGetItemId1(Item item){
        if (item instanceof ItemBlock) return 0;
        return item.itemID;
    }
}
