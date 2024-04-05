package net.xiaoyu233.fml.reload.transform.registry;

import net.minecraft.TileEntity;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.TileEntityRegisterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntity.class)
public abstract class TileEntityRegistryMixin {
    @Shadow
    protected static void addMapping(Class<?> par0Class, String par1Str) {

    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void injectRegister(CallbackInfo callbackInfo){
        MITEEvents.MITE_EVENT_BUS.post(new TileEntityRegisterEvent(TileEntityRegistryMixin::addMapping));
    }
}
