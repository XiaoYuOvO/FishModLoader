package net.xiaoyu233.fml.reload.transform;

import net.minecraft.ChatMessage;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.PlayerLoggedInEvent;
import net.xiaoyu233.fml.reload.utils.VersionCheckThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public class MinecraftServerTrans {
   @Shadow
   private static boolean treachery_detected;
   @Shadow
   private static int treachery_shutdown_counter;

   @Overwrite
   public static void setTreacheryDetected() {
      treachery_detected = false;
      treachery_shutdown_counter = 0;
   }

   @Overwrite
   public void playerLoggedIn(ServerPlayer par1EntityPlayerMP) {
      MITEEvents.MITE_EVENT_BUS.post(new PlayerLoggedInEvent(par1EntityPlayerMP));
      (new VersionCheckThread(par1EntityPlayerMP)).start();
   }

   @Shadow
   public void sendChatToPlayer(ChatMessage par1ChatMessageComponent) {
   }
}
