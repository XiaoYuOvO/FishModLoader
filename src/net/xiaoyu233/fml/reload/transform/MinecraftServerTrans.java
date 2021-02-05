package net.xiaoyu233.fml.reload.transform;

import net.minecraft.ChatMessage;
import net.minecraft.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.PlayerLoggedInEvent;
import net.xiaoyu233.fml.reload.utils.VersionCheckThread;

@Transform(MinecraftServer.class)
public class MinecraftServerTrans {
   @Link
   private static boolean treachery_detected;
   @Link
   private static int treachery_shutdown_counter;

   public static void setTreacheryDetected() {
      treachery_detected = false;
      treachery_shutdown_counter = 0;
   }

   @Marker
   public void a(ChatMessage par1ChatMessageComponent) {
   }

   public void playerLoggedIn(EntityPlayer par1EntityPlayerMP) {
      MITEEvents.MITE_EVENT_BUS.post(new PlayerLoggedInEvent(par1EntityPlayerMP));
      (new VersionCheckThread(par1EntityPlayerMP)).start();
   }
}
