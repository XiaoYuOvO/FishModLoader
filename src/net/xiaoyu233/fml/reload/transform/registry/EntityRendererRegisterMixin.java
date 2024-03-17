package net.xiaoyu233.fml.reload.transform.registry;

import net.minecraft.Entity;
import net.minecraft.Render;
import net.minecraft.RenderManager;
import net.xiaoyu233.fml.reload.event.EntityRendererRegistryEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RenderManager.class)
public class EntityRendererRegisterMixin {
    @Shadow private Map<Class<? extends Entity>, Render> entityRenderMap;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectInit(CallbackInfo callbackInfo){
        EntityRendererRegistryEvent entityRendererRegistryEvent = new EntityRendererRegistryEvent();
        MITEEvents.MITE_EVENT_BUS.post(entityRendererRegistryEvent);
        this.entityRenderMap.putAll(entityRendererRegistryEvent.getRendererMap());
        for(Render o : this.entityRenderMap.values()) {
            o.setRenderManager(ReflectHelper.dyCast(RenderManager.class, this));
        }
    }
}
