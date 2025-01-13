package net.xiaoyu233.fml.reload.transform;

import net.minecraft.DedicatedServer;
import net.minecraft.ServerPlayer;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.PlayerLoggedInEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "playerLoggedIn", at = @At("HEAD"))
    private void onPlayerLoggedIn(ServerPlayer par1EntityPlayerMP, CallbackInfo callbackInfo) {
        MITEEvents.MITE_EVENT_BUS.post(new PlayerLoggedInEvent(par1EntityPlayerMP));
    }
}
