package net.xiaoyu233.fml.reload.transform.network;

import net.minecraft.INetworkManager;
import net.minecraft.NetHandler;
import net.minecraft.NetLoginHandler;
import net.minecraft.Packet2ClientProtocol;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.network.FMLClientProtocol;
import net.xiaoyu233.fml.util.ModInfo;
import net.xiaoyu233.fml.util.RemoteModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


@Mixin(NetLoginHandler.class)
public abstract class PendingConnectionTransform extends NetHandler {
    @Shadow private String clientUsername;

    @Shadow public abstract void raiseErrorAndDisconnect(String par1Str);
    @Shadow
    private MinecraftServer mcServer;

    @Inject(method = "handleClientProtocol", at = @At("HEAD") ,cancellable = true)
    public void handleClientProtocol(Packet2ClientProtocol par1Packet2ClientProtocol, CallbackInfo callbackInfo) {
        FMLClientProtocol fmlClientProtocol = (FMLClientProtocol) par1Packet2ClientProtocol;
        if (fmlClientProtocol.getModInfos() != null && fmlClientProtocol.getSignatures().contains("FishModLoader")){
            StringBuilder problems = new StringBuilder();
            Map<String,ModInfo> serverMods = FishModLoader.getModsMapForLoginCheck();
            for (RemoteModInfo modInfo : fmlClientProtocol.getModInfos()) {
                String modid = modInfo.getModid();
                String modVerStr = modInfo.getModVerStr();
                boolean clientOnly = !modInfo.canBeUsedAt(MixinEnvironment.Side.SERVER);
                int modVerNum = modInfo.getModVerNum();
                if (!clientOnly|| !FishModLoader.isAllowsClientMods()) {
                    ModInfo oneMod = serverMods.get(modid);
                    if (oneMod != null) {
                        if (oneMod.getModVerNum() > modVerNum) {
                            problems.append("客户端模组版本过低:").append(modid).append(" 需要:").append(oneMod.getModVerStr()).append(" ,当前;").append(modVerStr).append(
                                    "\n");
                        } else if (oneMod.getModVerNum() < modVerNum) {
                            problems.append("客户端模组版本过高:").append(modid).append(" 需要:").append(oneMod.getModVerStr()).append(" ,当前;").append(modVerStr).append(
                                    "\n");
                        }
                        serverMods.remove(modid);
                    } else {
                        problems.append("客户端模组过多: ").append(modid).append("\n");
                        if (clientOnly){
                            problems.append("服务端不允许添加客户端模组").append(modid).append("\n");
                        }
                    }
                }else{
                    if (modid.contains("MITECoordinate")){
                        mcServer.getLogAgent().logInfo("Player " + this.clientUsername + "try to uses coordinate detector to join game!");
                    }
                }
            }
            if (!serverMods.isEmpty()){
                for (ModInfo value : serverMods.values()) {
                    problems.append("客户端缺失模组:").append(value.getModid() + "-" + value.getModVerStr()).append("\n");
                }
            }
            if (problems.length() > 0){
                this.raiseErrorAndDisconnect(problems.toString());
                callbackInfo.cancel();
            }
        }
    }

    @Override
    @Shadow
    public INetworkManager getNetManager() {
        return null;
    }

    @Override
    @Shadow
    public boolean isServerHandler() {
        return false;
    }

}