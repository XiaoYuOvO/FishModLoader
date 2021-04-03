package net.xiaoyu233.fml.reload.transform;

import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.reload.event.HandleChatCommandEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CommandHandler.class)
public class CommandHandlerTrans {

    @Inject(locals = LocalCapture.CAPTURE_FAILHARD ,method = "executeCommand",at = @At(value = "INVOKE_ASSIGN",shift = At.Shift.AFTER,target = "Lnet/minecraft/EnumCommand;get(Ljava/lang/String;)Lnet/minecraft/EnumCommand;"),cancellable = true)
    public void onCommandExecuted(ICommandListener par1ICommandSender, String par2Str,boolean permission_override, CallbackInfoReturnable<Integer> callbackInfo,MinecraftServer mc_server,WorldServer world, ServerPlayer player,EnumCommand command){
        HandleChatCommandEvent commandEvent = new HandleChatCommandEvent(par1ICommandSender,par2Str,player,world);
        MITEEvents.MITE_EVENT_BUS.post(commandEvent);
        if (commandEvent.isExecuteSuccess()){
            callbackInfo.setReturnValue(1);
        }
    }

//    public int executeCommand(ICommandListener par1ICommandSender, String par2Str, boolean permission_override) {}
}

