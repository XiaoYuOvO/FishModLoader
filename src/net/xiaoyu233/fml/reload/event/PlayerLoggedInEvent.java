package net.xiaoyu233.fml.reload.event;

import net.minecraft.EntityPlayer;

public class PlayerLoggedInEvent {
   private final EntityPlayer player;

   public PlayerLoggedInEvent(EntityPlayer player) {
      this.player = player;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }
}
