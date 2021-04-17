package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.EntityMinecartAbstract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityMinecartAbstract.class)
public class FixMinecart {
    @Shadow
    private static String[][] prevPosY;

    @Inject(method =  "<clinit>",at = @At(ordinal = 1,value = "FIELD",shift = At.Shift.BEFORE,target = "Lnet/minecraft/EntityMinecartAbstract;prevPosY*:[[Ljava/lang/String;"))
    private static void fixClassload(CallbackInfo callbackInfo){
        prevPosY = new String[0][0];
    }
}