package net.xiaoyu233.fml.reload.transform.registry;

import net.minecraft.CommandHandler;
import net.minecraft.ServerCommandManager;
import net.xiaoyu233.fml.reload.event.CommandRegisterEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandManager.class)
public abstract class ServerCommandManagerMixin extends CommandHandler {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectRegisterCommand(CallbackInfo callbackInfo){
        MITEEvents.MITE_EVENT_BUS.post(new CommandRegisterEvent(this::registerCommand));
    }
}
