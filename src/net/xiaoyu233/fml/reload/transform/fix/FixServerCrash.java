package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.WorldServer;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(WorldServer.class)
public class FixServerCrash {

    public void verifyWMs() {
//        if (this.t.i == 0 && !this.wms_checked) {
//            this.wm_value = 100;
//            this.verifyWM(0, 0);
//            this.verifyWM(-32, -32);
//            this.verifyWM(-32, 32);
//            this.verifyWM(32, -32);
//            this.verifyWM(32, 32);
//            if (this.wm_value != 200) {
//                System.exit(0);
//            }
//
//            this.wms_checked = true;
//        }
    }
}
