package net.xiaoyu233.fml.reload.transform.client;

import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.config.editor.ConfigEditor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityClientPlayerMP.class)
public abstract class ClientPlayerMixin extends ClientPlayer{

    public ClientPlayerMixin(Minecraft par1Minecraft, World par2World, Session par3Session, int par4) {
        super(par1Minecraft, par2World, par3Session, par4);
    }

    @Shadow public abstract void receiveChatMessage(String message, EnumChatFormatting color);

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    private void injectEditLocalClientConfig(String par1Str, boolean permission_override, CallbackInfo callbackInfo){
        if (par1Str.startsWith("/configs reload")){
            if (Minecraft.getMinecraft().getIntegratedServer() == null) {
                this.receiveChatMessage(("你不是局域网主机或服务器控制台,无法重载服务端配置,正在重载你的客户端配置"), EnumChatFormatting.YELLOW);
                FishModLoader.reloadAllConfigs();
                callbackInfo.cancel();
            }
        } else if (par1Str.startsWith("/configs edit")){
            if (Minecraft.getMinecraft().getIntegratedServer() == null) {
                this.receiveChatMessage(("你不是局域网主机或服务器控制台,无法更改服务端配置,你的更改只会在你的客户端生效"), EnumChatFormatting.YELLOW);
                new ConfigEditor(FishModLoader.getAllConfigRegistries()).setVisible(true);
                callbackInfo.cancel();
            }
        }
    }
}
