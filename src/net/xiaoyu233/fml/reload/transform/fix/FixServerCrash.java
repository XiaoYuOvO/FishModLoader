package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldServer.class)
public class FixServerCrash {
   @Overwrite
   public void verifyWMs() {
   }
}
