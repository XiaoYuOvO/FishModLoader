package net.xiaoyu233.fml.reload.transform;

import net.minecraft.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.asm.annotations.Transform;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.PlayerLoggedInEvent;
import net.xiaoyu233.fml.reload.utils.VersionCheckThread;

@Transform(MinecraftServer.class)
public class MinecraftServerTrans{
    public void playerLoggedIn(EntityPlayer par1EntityPlayerMP) {
        MITEEvents.MITE_EVENT_BUS.post(new PlayerLoggedInEvent(par1EntityPlayerMP));
        new VersionCheckThread(par1EntityPlayerMP).start();
    }
}
