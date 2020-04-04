package net.xiaoyu233.fml.reload.transform.network;

import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.annotations.Dist;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;
import net.xiaoyu233.fml.util.ModInfo;

import java.security.PublicKey;
import java.util.Map;
import java.util.Random;


@Transform(PendingConnection.class)
public class PendingConnectionTransform extends Connection {
    @Link
    private static Random c;
    @Link
    private byte[] d;
    @Link
    private MinecraftServer e;
    @Link
    public NetworkManager a;
    @Link
    private String g;
    @Link
    private String i;

    @Override
    @Marker
    public boolean a() {
        return false;
    }

    public void a(Packet2Handshake par1Packet2ClientProtocol) {
        if (this.g != null) {
            this.a("Quit repeating yourself!");
        } else {
            this.g = par1Packet2ClientProtocol.f();
            if (!this.g.equals(StripColor.a(this.g))) {
                this.a("Invalid username!");
            } else {
                PublicKey var2 = this.e.H().getPublic();
                if (par1Packet2ClientProtocol.d() != 78) {
                    if (par1Packet2ClientProtocol.d() > 78) {
                        this.a("Outdated server!");
                    } else {
                        this.a("Outdated client!");
                    }

                } else if ("1.6.4".equals(par1Packet2ClientProtocol.MC_version) && "R196".equals(par1Packet2ClientProtocol.MITE_release_number) && par1Packet2ClientProtocol.getB().contains("FishModLoader")) {
                    if (!this.e.af().d(this.g)) {
                        this.a("You are not white-listed on this server!");
                    } else {
                        if (DedicatedServer.isTournamentThatUsesAllottedTimes()) {
                            Long tick_of_disconnection = (Long)DedicatedServer.players_kicked_for_depleted_time_shares.get(this.g);
                            if (tick_of_disconnection != null) {
                                long current_tick = DedicatedServer.F().a(0).I();
                                if (current_tick - tick_of_disconnection < 72000L) {
                                    this.a("Please wait at least an hour for your time share to replenish");
                                    return;
                                }
                            }
                        }

                        if (DedicatedServer.disconnection_penalty_enabled) {
                            SoonestReconnectionTime srt = DedicatedServer.getSoonestReconnectionTime(this.g);
                            if (srt != null) {
                                World world = DedicatedServer.F().a(0);
                                long current_tick = world.I();
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
                                    this.a.a((new Packet85SimpleSignal(EnumSignal.reconnection_delay)).setByte(message_type).setShort(srt.adjusted_hour_of_disconnection).setInteger(seconds_delay));
                                    this.a("");
                                    return;
                                }
                            }
                        }
                        if (par1Packet2ClientProtocol.getModInfos() != null){
                            StringBuilder problems = new StringBuilder();
                            Map<String,ModInfo> serverMods = FishModLoader.getModsMapForLoginCheck();
                            for (ModInfo modInfo : par1Packet2ClientProtocol.getModInfos()) {
                                String modid = modInfo.getModid();
                                String modVerStr = modInfo.getModVerStr();
                                boolean clientOnly = !modInfo.canBeUsedAt(Dist.SERVER);
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
                                        e.getAuxLogAgent().a("Player " + this.g + "try to uses coordinate detector to join game!");
                                    }
                                }
                            }
                            if (!serverMods.isEmpty()){
                                for (ModInfo value : serverMods.values()) {
                                    problems.append("客户端缺失模组:").append(value.getModid() + "-" + value.getModVerStr()).append("\n");
                                }
                            }
                            if (problems.length() > 0){
                                this.a(problems.toString());
                            }
                        }

                        this.i = this.e.W() ? Long.toString(c.nextLong(), 16) : "-";
                        this.d = new byte[4];
                        c.nextBytes(this.d);
                        this.a.a(new Packet253KeyRequest(this.i, var2, this.d));
                    }
                } else if (!par1Packet2ClientProtocol.getB().contains("AntiCheat")){
                    this.a("客户端模组不完整,请联系服主重新安装");
                }else{
                    this.a("This server requires a 1.6.4-MITE R196 client.");
                }
            }
        }
    }

    @Override
    @Marker
    public INetworkManager getNetManager() {
        return null;
    }

    @Marker
    private void a(String s) {

    }
}