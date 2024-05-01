package net.xiaoyu233.fml.reload.transform.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;
import net.minecraft.INetworkManager;
import net.minecraft.NetHandler;
import net.minecraft.NetLoginHandler;
import net.minecraft.Packet2ClientProtocol;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.config.Configs;
import net.xiaoyu233.fml.network.FMLClientProtocol;
import net.xiaoyu233.fml.util.RemoteModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;


@Mixin(NetLoginHandler.class)
public abstract class PendingConnectionTransform extends NetHandler {
    @Shadow private String clientUsername;

    @Shadow public abstract void raiseErrorAndDisconnect(String par1Str);
    @Shadow
    private MinecraftServer mcServer;

    @Inject(method = "handleClientProtocol", at = @At("HEAD") ,cancellable = true)
    public void handleClientProtocol(Packet2ClientProtocol par1Packet2ClientProtocol, CallbackInfo callbackInfo) throws VersionParsingException {
        FMLClientProtocol fmlClientProtocol = (FMLClientProtocol) par1Packet2ClientProtocol;
        List<RemoteModInfo> modInfos = fmlClientProtocol.getModInfos();
        if (modInfos != null && fmlClientProtocol.getSignatures().contains("FishModLoader")){
            StringBuilder problems = new StringBuilder();
            Map<String, ModContainerImpl> serverMods = FishModLoader.getModsMapForLoginCheck();
            for (RemoteModInfo modInfo : modInfos) {
                String modid = modInfo.getModid();
                boolean clientOnly = !modInfo.canBeUsedAt(EnvType.SERVER);
                Version clientModVer = modInfo.getModVer();
                ModContainerImpl serverMod = serverMods.get(modid);
                if (serverMod != null) {
                    LoaderModMetadata serverModInfo = serverMod.getMetadata();
                    if (serverModInfo.getVersion().compareTo(clientModVer) > 0) {
                        problems.append("客户端模组版本过低:").append(modid).append(" 需要:").append(serverModInfo.getVersion()).append(" ,当前;").append(clientModVer).append("\n");
                    } else if (serverModInfo.getVersion().compareTo(clientModVer) < 0) {
                        problems.append("客户端模组版本过高:").append(modid).append(" 需要:").append(serverModInfo.getVersion()).append(" ,当前;").append(clientModVer).append("\n");
                    }
                    serverMods.remove(modid);
                } else {
                    if (clientOnly) {
                        if (!Configs.Server.ALLOW_CLIENT_MODS.get()) {
                            problems.append("服务端不允许添加客户端模组: ").append(modid).append("\n");
                        }
                    } else {
                        problems.append("客户端模组过多: ").append(modid).append("\n");
                    }
                }
            }
            if (!serverMods.isEmpty()){
                for (ModContainerImpl value : serverMods.values()) {
                    if (value.getMetadata().getEnvironment() == ModEnvironment.CLIENT){
                        continue;
                    }
                    LoaderModMetadata modMetadata = value.getMetadata();
                    problems.append("客户端缺失模组:")
                            .append(modMetadata.getId())
                            .append("-")
                            .append(modMetadata.getVersion())
                            .append("\n");
                }
            }
            if (!problems.isEmpty()){
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