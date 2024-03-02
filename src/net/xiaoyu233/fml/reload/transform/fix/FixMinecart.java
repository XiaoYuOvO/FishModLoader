package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.EntityMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityMinecart.class)
public class FixMinecart {
    @Shadow
    private static String[][] s;

    @Inject(method =  "<clinit>",at = @At(ordinal = 1,value = "FIELD",shift = At.Shift.BEFORE,target = "Lnet/minecraft/EntityMinecart;s:[[Ljava/lang/String;"))
    private static void fixClassload(CallbackInfo callbackInfo){
        s = new String[0][0];
    }
}