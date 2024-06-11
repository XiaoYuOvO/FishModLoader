package net.xiaoyu233.fml.reload.transform.api;

import net.minecraft.ItemTool;
import net.minecraft.Material;
import net.xiaoyu233.fml.api.item.CustomMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemTool.class)
public class ItemToolMixin {
    @Shadow private Material effective_material;

    @Inject(method = "getMaterialHarvestEfficiency", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;setErrorMessage(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
    private void injectToolCustomMaterialEffective(CallbackInfoReturnable<Float> callbackInfoReturnable){
        if (this.effective_material instanceof CustomMaterial customMaterial){
            callbackInfoReturnable.setReturnValue(customMaterial.getToolEffective());
        }
    }
}
