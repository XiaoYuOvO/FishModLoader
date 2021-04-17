package net.xiaoyu233.fml.reload.transform.network;

import net.minecraft.Packet;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.PacketRegisterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Packet.class)
public class PacketTrans {
    @Shadow
    static void addIdClassMapping(int par0, boolean par1, boolean par2, Class par3Class){}
    @Inject(
            method = "<clinit>",
    at = @At("RETURN"))
    private static void injectRegisterPacket(CallbackInfo callback){
        MITEEvents.MITE_EVENT_BUS.post(new PacketRegisterEvent(PacketTrans::addIdClassMapping));
    }


}
