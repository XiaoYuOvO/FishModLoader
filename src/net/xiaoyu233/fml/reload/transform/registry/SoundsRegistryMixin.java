package net.xiaoyu233.fml.reload.transform.registry;

import net.minecraft.SoundsMITE;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.SoundsRegisterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundsMITE.class)
public abstract class SoundsRegistryMixin {
    @Shadow protected abstract boolean add(String path);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectRegister(CallbackInfo callbackInfo){
        MITEEvents.MITE_EVENT_BUS.post(new SoundsRegisterEvent(this::add));
    }
}
