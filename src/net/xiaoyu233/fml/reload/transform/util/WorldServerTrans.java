package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.WorldServer;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Transform;

import java.util.Set;

@Transform(WorldServer.class)
public class WorldServerTrans {
   @Link
   protected Set G;

   public Set getG() {
      return this.G;
   }
}
