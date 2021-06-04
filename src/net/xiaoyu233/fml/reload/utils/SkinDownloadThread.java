package net.xiaoyu233.fml.reload.utils;

import net.minecraft.Minecraft;
import net.minecraft.bic;
import net.xiaoyu233.fml.reload.transform.fix.skin.BicAccessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinDownloadThread extends Thread {
   final bic a;

   public SkinDownloadThread(bic texture) {
      this.a = texture;
   }

   public void run() {
      HttpURLConnection var1 = null;

      try {
         var1 = (HttpURLConnection)(new URL(MojangAPI.fixImageUrl(((BicAccessor)this.a).getB()))).openConnection(Minecraft.w().I());
         var1.setDoInput(true);
         var1.setDoOutput(false);
         var1.connect();
         if (var1.getResponseCode() / 100 == 2) {
            BufferedImage var2 = ImageIO.read(var1.getInputStream());
            if (((BicAccessor)this.a).getC() != null) {
               var2 = ((BicAccessor)this.a).getC().a(var2);
            }

            this.a.a(var2);
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
