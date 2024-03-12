package net.xiaoyu233.fml.reload.transform.registry;

import net.minecraft.EntityList;
import net.xiaoyu233.fml.reload.event.EntityRegisterEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityList.class)
public class EntityRegistryMixin {

    @Shadow
    private static void addMapping(Class par0Class, String par1Str, int par2) {}

    @Shadow
    private static void addMapping(Class par0Class, String par1Str, int par2, int par3, int par4) {}

    @Inject(method = "<clinit>",at = @At("RETURN"))
    private static void injectClinit(CallbackInfo ci){
        MITEEvents.MITE_EVENT_BUS.post(new EntityRegisterEvent(EntityRegistryMixin::addMapping, EntityRegistryMixin::addMapping));
    }
}
