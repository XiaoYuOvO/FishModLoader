package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.World;
import net.minecraft.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public class WorldTrans {
   @Shadow
   public WorldProvider provider;

   public WorldProvider getProvider() {
      return this.provider;
   }
}
