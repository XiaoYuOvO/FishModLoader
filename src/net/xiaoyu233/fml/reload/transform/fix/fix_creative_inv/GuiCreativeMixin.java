package net.xiaoyu233.fml.reload.transform.fix.fix_creative_inv;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainerCreative.class)
public abstract class GuiCreativeMixin extends InventoryEffectRenderer {
    public GuiCreativeMixin(Container par1Container) {
        super(par1Container);
    }

//    @WrapOperation(method = "handleMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/Container;slotClick(IIIZLnet/minecraft/EntityPlayer;)Lnet/minecraft/ItemStack;", ordinal = 0))
//    private ItemStack wrapClick(Container container, int par1, int par2, int par3, boolean holding_shift, EntityPlayer par4EntityPlayer, Operation<ItemStack> original, @Share(value = "clickedStack") LocalRef<ItemStack> stack){
//        ItemStack itemStack = original.call(container, par1, par2, par3, holding_shift, par4EntityPlayer);
//        stack.set(itemStack);
//        return itemStack;
//    }
    @Inject(method = "handleMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/Container;slotClick(IIIZLnet/minecraft/EntityPlayer;)Lnet/minecraft/ItemStack;", shift = At.Shift.AFTER, ordinal = 0))
    private void injectUnlock(Slot par1Slot, int par2, int par3, int par4, CallbackInfo callbackInfo, @Share(value = "clickedStack") LocalRef<ItemStack> stack){
        //TODO
//        short var6 = this.mc.thePlayer.openContainer.getNextTransactionID(this.mc.thePlayer.inventory);
//        this.mc.thePlayer.getNetManager().addToSendQueue(new Packet102WindowClick(this.mc.thePlayer.inventoryContainer.windowId, par2, par3, par4, stack.get(), var6).setHoldingShift(GuiScreen.isShiftKeyDown()));
        this.mc.thePlayer.inventoryContainer.unlockAllSlots();
        this.mc.thePlayer.inventoryContainer.unlockNextTick();
    }

    @Inject(method = "updateScreen", at = @At("RETURN"))
    public void updateUnlock(CallbackInfo callbackInfo) {
        this.mc.thePlayer.inventoryContainer.unlockAllSlots();
    }
}
