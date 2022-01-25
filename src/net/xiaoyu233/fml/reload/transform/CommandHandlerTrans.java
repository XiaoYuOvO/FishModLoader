package net.xiaoyu233.fml.reload.transform;

import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.config.editor.ConfigEditor;
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
        if (par2Str.startsWith("configs reload")){
            mc_server.sendChatToPlayer(ChatMessage.createFromText("[Server] 正在重载所有配置文件"));
            FishModLoader.reloadAllConfigs();
            callbackInfo.setReturnValue(1);
        }
        if (par2Str.startsWith("configs edit")){
            if (player != null){
                player.sendChatToPlayer(ChatMessage.createFromText("正在打开配置文件编辑器..."));
            }else {
                System.out.println("正在打开配置文件编辑器...");
            }
            new ConfigEditor(FishModLoader.getAllConfigRegistries()).setVisible(true);
            callbackInfo.setReturnValue(1);
        }
    }

//    public int executeCommand(ICommandListener par1ICommandSender, String par2Str, boolean permission_override) {}
}

