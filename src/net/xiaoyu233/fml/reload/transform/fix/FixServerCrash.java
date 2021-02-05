package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.WorldServer;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(WorldServer.class)
public class FixServerCrash {
   public void verifyWMs() {
   }
}
