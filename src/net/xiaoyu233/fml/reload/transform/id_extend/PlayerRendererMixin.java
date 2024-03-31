package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.ItemBlock;
import net.minecraft.ItemStack;
import net.minecraft.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderPlayer.class)
public class PlayerRendererMixin {
    @Redirect(method = "renderSpecials", at = @At(value = "FIELD", target = "Lnet/minecraft/ItemStack;itemID:I", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/InventoryPlayer;getCurrentItemStack()Lnet/minecraft/ItemStack;"),to = @At(value = "CONSTANT", args = "classValue=net/minecraft/ItemBow")))
    private int redirectGetItemId(ItemStack stack){
        if (stack.getItem() instanceof ItemBlock) return 0;
        return stack.itemID;
    }

    @Redirect(method = "renderSpecials", at = @At(value = "FIELD", target = "Lnet/minecraft/ItemStack;itemID:I", ordinal = 0), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/AbstractClientPlayer;getCommandSenderName()Ljava/lang/String;")))
    private int redirectGetItemId1(ItemStack stack){
        if (stack.getItem() instanceof ItemBlock) return 0;
        return stack.itemID;
    }
}
