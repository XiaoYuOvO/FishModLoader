package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.EntityRenderer;
import net.xiaoyu233.fml.FishModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityRenderer.class)
public class FpsUnlimit2 {
    @Overwrite
    public static int a(int par0) {
        return FishModLoader.getFpsLimit();
    }
}
