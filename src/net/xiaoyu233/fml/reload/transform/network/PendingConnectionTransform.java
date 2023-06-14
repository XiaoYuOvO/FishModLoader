package net.xiaoyu233.fml.reload.transform.network;

import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.ModInfo;
import net.xiaoyu233.fml.util.RemoteModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Mixin(PendingConnection.class)
public abstract class PendingConnectionTransform extends Connection {
    @Shadow
    private static Random rand;
    @Shadow
    public NetworkManager myTCPConnection;
    @Shadow
    private String clientUsername;
    @Shadow
    private String loginServerId;
    @Shadow
    private MinecraftServer mcServer;
    @Shadow
    private byte[] verifyToken;

    @Overwrite
    public void handleClientProtocol(Packet2Handshake par1Packet2ClientProtocol) {
        if (this.clientUsername != null) {
            this.kickUser("Quit repeating yourself!");
        } else {
            this.clientUsername = par1Packet2ClientProtocol.getUsername();
            if (!this.clientUsername.equals(StripColor.stripControlCodes(this.clientUsername))) {
                this.kickUser("Invalid username!");
            } else {
                PublicKey var2 = this.mcServer.H().getPublic();
                if (par1Packet2ClientProtocol.getProtocolVersion() != 78) {
                    if (par1Packet2ClientProtocol.getProtocolVersion() > 78) {
                        this.kickUser("Outdated server!");
                    } else {
                        this.kickUser("Outdated client!");
                    }

                } else if ("1.6.4".equals(par1Packet2ClientProtocol.MC_version) && "R196".equals(par1Packet2ClientProtocol.MITE_release_number) && par1Packet2ClientProtocol.getSignatures().contains("FishModLoader")) {
                    if (!this.mcServer.getConfigurationManager().isAllowedToLogin(this.clientUsername)) {
                        this.kickUser("You are not white-listed on this server!");
                    } else {
                        if (DedicatedServer.isTournamentThatUsesAllottedTimes()) {
                            Long tick_of_disconnection = (Long)DedicatedServer.players_kicked_for_depleted_time_shares.get(this.clientUsername);
                            if (tick_of_disconnection != null) {
                                long current_tick = DedicatedServer.F().a(0).getTotalWorldTime();
                                if (current_tick - tick_of_disconnection < 72000L) {
                                    this.kickUser("Please wait at least an hour for your time share to replenish");
                                    return;
                                }
                            }
                        }

                        if (DedicatedServer.disconnection_penalty_enabled) {
                            SoonestReconnectionTime srt = DedicatedServer.getSoonestReconnectionTime(this.clientUsername);
                            if (srt != null) {
                                World world = DedicatedServer.F().a(0);
                                long current_tick = world.getTotalWorldTime();
                                srt.ticks_disconnected += Math.max(current_tick - srt.tick_of_disconnection, 0L);
                                boolean reconnection_prevented = true;
                                int hour_of_latest_reconnection = World.getHourOfLatestReconnection();
                                if (world.getHourOfDay() == hour_of_latest_reconnection) {
                                    reconnection_prevented = false;
                                    srt.ticks_disconnected = 0L;
                                } else if (srt.ticks_disconnected <= 600L) {
                                    reconnection_prevented = false;
                                } else if (current_tick >= srt.soonest_reconnection_tick) {
                                    reconnection_prevented = world.getHourOfDay() < srt.adjusted_hour_of_disconnection || world.getHourOfDay() > hour_of_latest_reconnection;
                                    if (!reconnection_prevented) {
                                        srt.ticks_disconnected = 0L;
                                    }
                                }

                                if (reconnection_prevented) {
                                    int message_type = 1;

                                    int ticks_to_wait;
                                    ticks_to_wait = (int)(srt.soonest_reconnection_tick - current_tick);
                                    while (ticks_to_wait <= 0) {
                                        ticks_to_wait += 24000;
                                    }

                                    int ticks_until_hour_of_latest_reconnection = hour_of_latest_reconnection * 1000 - world.getAdjustedTimeOfDay();
                                    if (ticks_until_hour_of_latest_reconnection < 0) {
                                        ticks_until_hour_of_latest_reconnection += 24000;
                                    }

                                    if (ticks_until_hour_of_latest_reconnection < ticks_to_wait) {
                                        ticks_to_wait = ticks_until_hour_of_latest_reconnection;
                                        message_type = 2;
                                    }

                                    int seconds_delay = ticks_to_wait / 20;
                                    this.myTCPConnection.addToSendQueue((new Packet85SimpleSignal(EnumSignal.reconnection_delay)).setByte(message_type).setShort(srt.adjusted_hour_of_disconnection).setInteger(seconds_delay));
                                    this.kickUser("");
                                    return;
                                }
                            }
                        }
                        if (par1Packet2ClientProtocol.getModInfos() != null){
                            StringBuilder problems = new StringBuilder();
                            Map<String,ModInfo> serverMods = FishModLoader.getModsMapForLoginCheck();
                            for (RemoteModInfo modInfo : ((List<RemoteModInfo>) par1Packet2ClientProtocol.getModInfos())) {
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
                                this.kickUser(problems.toString());
                            }
                        }

                        this.loginServerId = this.mcServer.W() ? Long.toString(rand.nextLong(), 16) : "-";
                        this.verifyToken = new byte[4];
                        rand.nextBytes(this.verifyToken);
                        this.myTCPConnection.addToSendQueue(new Packet253KeyRequest(this.loginServerId, var2, this.verifyToken));
                    }
                } else if (!par1Packet2ClientProtocol.getSignatures().contains("AntiCheat")){
                    this.kickUser("客户端模组不完整,请联系服主重新安装");
                }else{
                    this.kickUser("This server requires a 1.6.4-MITE R196 client.");
                }
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

    @Shadow
    private void kickUser(String s) {

    }
}