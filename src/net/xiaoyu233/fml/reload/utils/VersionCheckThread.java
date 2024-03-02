package net.xiaoyu233.fml.reload.utils;

import net.minecraft.ChatMessageComponent;
import net.minecraft.I18n;
import net.minecraft.ServerPlayer;
import net.xiaoyu233.fml.FishModLoader;

public class VersionCheckThread extends Thread {
   private final ServerPlayer par1EntityPlayerMP;

   public VersionCheckThread(ServerPlayer player) {
      this.par1EntityPlayerMP = player;
   }

   public void run() {
      String onlineVersion = FishModLoader.getOnlineVersion();
      if (onlineVersion == null) {
         this.par1EntityPlayerMP.sendChatToPlayer(ChatMessageComponent.createFromText(I18n.getString("fishmodloader.update.offline")));
      } else if (!onlineVersion.equals(FishModLoader.VERSION)) {
         this.par1EntityPlayerMP.sendChatToPlayer(ChatMessageComponent.createFromText(I18n.getStringParams("fishmodloader.update.available", onlineVersion)));
      }

   }
}
