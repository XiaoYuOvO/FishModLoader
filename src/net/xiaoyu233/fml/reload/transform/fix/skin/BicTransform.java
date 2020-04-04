package net.xiaoyu233.fml.reload.transform.fix.skin;

import net.minecraft.*;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;
import net.xiaoyu233.fml.reload.utils.SkinDownloadThread;
import net.xiaoyu233.fml.util.ReflectHelper;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Transform(bic.class)
public class BicTransform extends bia{
    @Link
    private String b;
    @Link
    private bfi c;
    @Link
    private BufferedImage d;
    @Link
    private Thread e;
    @Link
    private bif f;

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

    @Marker
    public int b() {
        return 0;
    }

    public String getB() {
        return this.b;
    }

    public bfi getC() {
        return this.c;
    }
}
