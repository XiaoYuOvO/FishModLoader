package net.xiaoyu233.fml.reload.transform.client;

import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.config.editor.ConfigEditor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayer.class)
public abstract class ClientPlayerMixin extends bex{
    @Shadow public NetClientHandler netClientHandler;

    public ClientPlayerMixin(Minecraft par1Minecraft, World par2World, PlayerNameSession par3Session, int par4) {
        super(par1Minecraft, par2World, par3Session, par4);
    }

    @Shadow public abstract void receiveChatMessage(String message, EnumChatFormat color);

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void injectEditLocalClientConfig(String par1Str, boolean permission_override, CallbackInfo callbackInfo){
        if (par1Str.startsWith("/configs reload")){
            if (Minecraft.w().C() == null) {
                this.receiveChatMessage(("你不是局域网主机或服务器控制台,无法重载服务端配置,正在重载你的客户端配置"), EnumChatFormat.YELLOW);
                FishModLoader.reloadAllConfigs();
                callbackInfo.cancel();
            }
        } else if (par1Str.startsWith("/configs edit")){
            if (Minecraft.w().C() == null) {
                this.receiveChatMessage(("你不是局域网主机或服务器控制台,无法更改服务端配置,你的更改只会在你的客户端生效"), EnumChatFormat.YELLOW);
                new ConfigEditor(FishModLoader.getAllConfigRegistries()).setVisible(true);
                callbackInfo.cancel();
            }
        }
    }
}
