package net.xiaoyu233.fml.reload.transform.fix.skin;

import net.minecraft.*;
import net.xiaoyu233.fml.reload.utils.SkinDownloadThread;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Mixin(bic.class)
public abstract class BicTransform extends bia {
   @Shadow
   @Final
   public String b;
   @Shadow
   private BufferedImage d;
   @Shadow
   private Thread e;
   @Shadow
   private bif f;

   @Overwrite
   public void a(bjp parambjp) {
      if (this.d == null) {
         if (this.f != null) {
            try {
               this.f.a(parambjp);
            } catch (IOException var3) {
               var3.printStackTrace();
            }

            super.a = this.f.b();
         }
      } else {
         bip.a(this.b(), this.d);
      }

      if (this.e == null) {
         this.e = new SkinDownloadThread(ReflectHelper.dyCast(this));
         this.e.setDaemon(true);
         this.e.setName("Skin downloader: " + this.b);
         this.e.start();
      }

   }

   @Shadow
   public int b() {
      return 0;
   }
}
