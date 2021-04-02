package net.xiaoyu233.fml.reload.transform.network;

import net.minecraft.*;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.PacketRegisterEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Transform(Packet.class)
public class PacketTrans {
    @Link
    public static IntHashMap l = new IntHashMap();
    @Link
    private static final Map a = new HashMap();
    @Link
    private static final Set b = new HashSet();
    @Link
    private static final Set c = new HashSet();
    static {
        a(0, true, true, Packet0KeepAlive.class);
        a(1, true, true, Packet1Login.class);
        a(2, false, true, Packet2Handshake.class);
        a(3, true, true, Packet3Chat.class);
        a(4, true, false, Packet4UpdateTime.class);
        a(5, true, true, Packet5EntityEquipment.class);
        a(6, true, false, Packet6SpawnPosition.class);
        a(8, true, false, Packet8UpdateHealth.class);
        a(9, true, true, Packet9Respawn.class);
        a(10, true, true, Packet10Flying.class);
        a(11, true, true, Packet11PlayerPosition.class);
        a(12, true, true, Packet12PlayerLook.class);
        a(13, true, true, Packet13PlayerLookMove.class);
        a(14, false, true, Packet14BlockDig.class);
        a(15, false, true, Packet15Place.class);
        a(16, true, true, Packet16BlockItemSwitch.class);
        a(17, true, false, Packet17EntityLocationAction.class);
        a(18, true, true, Packet18ArmAnimation.class);
        a(19, false, true, Packet19EntityAction.class);
        a(20, true, false, Packet20NamedEntitySpawn.class);
        a(22, true, false, Packet22Collect.class);
        a(23, true, false, Packet23VehicleSpawn.class);
        a(24, true, false, Packet24MobSpawn.class);
        a(25, true, false, Packet25EntityPainting.class);
        a(26, true, false, Packet26AddExpOrb.class);
        a(27, false, true, Packet27PlayerInput.class);
        a(28, true, false, Packet28EntityVelocity.class);
        a(29, true, false, Packet29DestroyEntity.class);
        a(30, true, false, Packet30Entity.class);
        a(31, true, false, Packet31RelEntityMove.class);
        a(32, true, false, Packet32EntityLook.class);
        a(33, true, false, Packet33RelEntityMoveLook.class);
        a(34, true, true, Packet34EntityTeleport.class);
        a(35, true, false, Packet35EntityHeadRotation.class);
        a(38, true, false, Packet38EntityStatus.class);
        a(39, true, false, Packet39AttachEntity.class);
        a(40, true, false, Packet40EntityMetadata.class);
        a(41, true, false, Packet41MobEffect.class);
        a(42, true, false, Packet42RemoveMobEffect.class);
        a(43, true, false, Packet43SetExperience.class);
        a(44, true, false, Packet44UpdateAttributes.class);
        a(51, true, false, Packet51MapChunk.class);
        a(52, true, false, Packet52MultiBlockChange.class);
        a(53, true, false, Packet53BlockChange.class);
        a(54, true, false, Packet54PlayNoteBlock.class);
        a(55, true, true, Packet55BlockBreakAnimation.class);
        a(56, true, false, Packet56MapChunkBulk.class);
        a(60, true, false, Packet60Explosion.class);
        a(61, true, false, Packet61WorldEvent.class);
        a(62, true, false, Packet62NamedSoundEffect.class);
        a(70, true, false, Packet70Bed.class);
        a(71, true, false, Packet71Weather.class);
        a(80, true, false, Packet80LongDistanceSound.class);
        a(81, false, true, Packet81RightClick.class);
        a(82, false, true, Packet82AddHunger.class);
        a(83, true, false, Packet83EntityTeleportCompact.class);
        a(84, true, false, Packet84EntityStateWithData.class);
        a(85, true, true, Packet85SimpleSignal.class);
        a(86, true, false, Packet86EntityTeleportWithMotion.class);
        a(87, false, true, Packet87SetDespawnCounters.class);
        a(88, true, false, Packet88UpdateStrongboxOwner.class);
        a(89, false, true, Packet89PlaySoundOnServerAtEntity.class);
        a(90, false, true, Packet90BroadcastToAssociatedPlayers.class);
        a(91, true, false, Packet91PlayerStat.class);
        a(92, true, false, Packet92UpdateTimeSmall.class);
        a(93, true, false, Packet93WorldAchievement.class);
        a(94, true, false, Packet94CreateFile.class);
        a(97, true, false, Packet97MultiBlockChange.class);
        a(100, true, false, Packet100OpenWindow.class);
        a(101, true, true, Packet101CloseWindow.class);
        a(102, false, true, Packet102WindowClick.class);
        a(103, true, false, Packet103SetSlot.class);
        a(104, true, false, Packet104WindowItems.class);
        a(105, true, false, Packet105CraftProgressBar.class);
        a(106, true, true, Packet106Transaction.class);
        a(107, true, true, Packet107SetCreativeSlot.class);
        a(108, false, true, Packet108ButtonClick.class);
        a(130, true, true, Packet130UpdateSign.class);
        a(131, true, false, Packet131ItemData.class);
        a(132, true, false, Packet132TileEntityData.class);
        a(133, true, false, Packet133OpenTileEntity.class);
        a(200, true, false, Packet200Statistic.class);
        a(201, true, false, Packet201PlayerInfo.class);
        a(202, true, true, Packet202Abilities.class);
        a(203, true, true, Packet203TabComplete.class);
        a(204, false, true, Packet204LocaleAndViewDistance.class);
        a(205, false, true, Packet205ClientCommand.class);
        a(206, true, false, Packet206SetScoreboardObjective.class);
        a(207, true, false, Packet207SetScoreboardScore.class);
        a(208, true, false, Packet208SetScoreboardDisplayObjective.class);
        a(209, true, false, Packet209SetScoreboardTeam.class);
        a(250, true, true, Packet250CustomPayload.class);
        a(252, true, true, Packet252KeyResponse.class);
        a(253, true, false, Packet253KeyRequest.class);
        a(254, false, true, Packet254GetInfo.class);
        a(255, true, true, Packet255KickDisconnect.class);
        MITEEvents.MITE_EVENT_BUS.post(new PacketRegisterEvent((PacketTrans::a)));
    }

    @Marker
    static void a(int par0, boolean par1, boolean par2, Class par3Class) {}
}
