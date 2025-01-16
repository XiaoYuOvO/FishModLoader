package net.xiaoyu233.fml.reload.transform.network;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.Packet;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.PacketRegisterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataInput;

@Mixin(Packet.class)
public class PacketTrans {
    @Shadow
    static void addIdClassMapping(int par0, boolean par1, boolean par2, Class par3Class){}
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void injectRegisterPacket(CallbackInfo callback){
        MITEEvents.MITE_EVENT_BUS.post(new PacketRegisterEvent(PacketTrans::addIdClassMapping));
    }

    @Inject(method = "readString", at = @At("HEAD"), require = 1)
    private static void injectRemoveStringReadLengthCheck(DataInput par0DataInput, int par1, CallbackInfoReturnable<String> callbackInfo, @Local(argsOnly = true) LocalIntRef maxLength){
        maxLength.set(Integer.MAX_VALUE);
    }
}
