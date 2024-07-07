package net.xiaoyu233.fml.reload.transform.registry;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Entity;
import net.minecraft.EntityList;
import net.xiaoyu233.fml.reload.event.EntityRegisterEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.utils.EntityUtil;
import net.xiaoyu233.fml.util.WriteLockField;
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
        ImmutableMap.Builder<Class<? extends Entity>, WriteLockField<String>> namespaceMapBuilder = new ImmutableMap.Builder<>();
        MITEEvents.MITE_EVENT_BUS.post(new EntityRegisterEvent((par0Class, namespace, name, id) -> {
            namespaceMapBuilder.put(par0Class, WriteLockField.createLocked(namespace));
            addMapping(par0Class, name, id);
        }, (par0Class1, namespace, name, par21, par3, par4) -> {
            namespaceMapBuilder.put(par0Class1, WriteLockField.createLocked(namespace));
            addMapping(par0Class1, name, par21, par3, par4);
        }));
        EntityUtil.initEntityNamespaceMap(namespaceMapBuilder.build());
    }
}
