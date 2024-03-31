package net.xiaoyu233.fml.reload.transform.fix.fix_creative_inv;

import net.minecraft.Container;
import net.minecraft.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.ContainerCreative")
public abstract class ContainerCreativeMixin extends Container {

    public ContainerCreativeMixin(EntityPlayer player) {
        super(player);
    }

    @Override
    public void lockSlotsThatChanged() {

    }
}
