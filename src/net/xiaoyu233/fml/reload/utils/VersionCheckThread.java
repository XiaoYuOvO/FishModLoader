package net.xiaoyu233.fml.reload.utils;

import net.minecraft.ChatMessage;
import net.minecraft.EntityPlayer;
import net.minecraft.LocaleI18n;
import net.xiaoyu233.fml.FishModLoader;

public class VersionCheckThread extends Thread {
   private final EntityPlayer par1EntityPlayerMP;

   public VersionCheckThread(EntityPlayer player) {
      this.par1EntityPlayerMP = player;
   }

   public void run() {
      String onlineVersion = FishModLoader.getOnlineVersion();
      if (onlineVersion == null) {
         this.par1EntityPlayerMP.a(ChatMessage.e(LocaleI18n.a("fishmodloader.update.offline")));
      } else if (!onlineVersion.equals("B0.1.4")) {
         this.par1EntityPlayerMP.a(ChatMessage.e(LocaleI18n.a("fishmodloader.update.available", onlineVersion)));
      }

   }
}
