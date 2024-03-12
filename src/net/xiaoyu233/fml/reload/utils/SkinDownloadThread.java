package net.xiaoyu233.fml.reload.utils;

import net.minecraft.Minecraft;
import net.minecraft.ThreadDownloadImageData;
import net.xiaoyu233.fml.relaunch.Launch;
import net.xiaoyu233.fml.reload.transform.fix.skin.ThreadDownloadImageDataAccessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinDownloadThread extends Thread {
   final ThreadDownloadImageData texture;

   public SkinDownloadThread(ThreadDownloadImageData texture) {
      this.setContextClassLoader(Launch.knotLoader.getClassLoader());
      this.texture = texture;
   }

   public void run() {
      HttpURLConnection var1 = null;

      try {
         var1 = (HttpURLConnection)(new URL(MojangAPI.fixImageUrl(((ThreadDownloadImageDataAccessor)this.texture).getImageUrl()))).openConnection(Minecraft.getMinecraft().getProxy());
         var1.setDoInput(true);
         var1.setDoOutput(false);
         var1.connect();
         if (var1.getResponseCode() / 100 == 2) {
            BufferedImage image = ImageIO.read(var1.getInputStream());
            if (((ThreadDownloadImageDataAccessor)this.texture).getImageBuffer() != null) {
               image = ((ThreadDownloadImageDataAccessor)this.texture).getImageBuffer().parseUserSkin(image);
            }

            this.texture.setBufferedImage(image);
         }
      } catch (Exception var6) {
         if (var1 != null) {
            System.err.println("Unable to connect to " + var1.getURL());
         }
      } finally {
         if (var1 != null) {
            var1.disconnect();
         }

      }

   }

//   static String a(bic par0ThreadDownloadImageData) {
//      return ReflectHelper.dyCast(bic.class, par0ThreadDownloadImageData).getUsername();
//   }
//
//   static bfi b(bic par0ThreadDownloadImageData) {
//      return ReflectHelper.dyCast(bic.class, par0ThreadDownloadImageData).getC();
//   }
}
