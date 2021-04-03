package net.xiaoyu233.fml.reload.utils;

import net.minecraft.ChatMessage;
import net.minecraft.LocaleI18n;
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
         this.par1EntityPlayerMP.sendChatToPlayer(ChatMessage.createFromText(LocaleI18n.translateToLocal("fishmodloader.update.offline")));
      } else if (!onlineVersion.equals(FishModLoader.VERSION)) {
         this.par1EntityPlayerMP.sendChatToPlayer(ChatMessage.createFromText(LocaleI18n.translateToLocalFormatted("fishmodloader.update.available", onlineVersion)));
      }

   }
}
