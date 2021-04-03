package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.Minecraft;
import net.xiaoyu233.fml.FishModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Minecraft.class)
public class FpsUnlimited {
   @Overwrite
   private int U() {
      return FishModLoader.getFpsLimit();
   }
}
