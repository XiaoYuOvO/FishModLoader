package net.xiaoyu233.fml.reload.event;

import net.minecraft.ServerPlayer;

public class PlayerLoggedInEvent {
   private final ServerPlayer player;

   public PlayerLoggedInEvent(ServerPlayer player) {
      this.player = player;
   }

   public ServerPlayer getPlayer() {
      return this.player;
   }
}
