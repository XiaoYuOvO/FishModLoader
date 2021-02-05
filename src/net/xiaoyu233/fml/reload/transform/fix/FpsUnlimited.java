package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.Minecraft;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(Minecraft.class)
public class FpsUnlimited {
   private int U() {
      return FishModLoader.getFpsLimit();
   }
}
