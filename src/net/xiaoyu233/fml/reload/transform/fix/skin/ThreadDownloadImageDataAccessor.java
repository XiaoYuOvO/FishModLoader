package net.xiaoyu233.fml.reload.transform.fix.skin;

import net.minecraft.IImageBuffer;
import net.minecraft.ThreadDownloadImageData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThreadDownloadImageData.class)
public interface ThreadDownloadImageDataAccessor {
    @Accessor(value = "imageUrl")
    String getImageUrl();

    @Accessor(value = "imageBuffer")
    IImageBuffer getImageBuffer();
}
