package net.xiaoyu233.fml.reload.event;

import net.minecraft.TileEntity;
import net.minecraft.TileEntityRenderer;

import java.util.function.BiConsumer;

public class TileEntityRendererRegisterEvent {
    private final BiConsumer<Class<? extends TileEntity>, TileEntityRenderer> rendererRegisterer;

    public TileEntityRendererRegisterEvent(BiConsumer<Class<? extends TileEntity>, TileEntityRenderer> rendererRegisterer) {
        this.rendererRegisterer = rendererRegisterer;
    }

    public void register(Class<? extends TileEntity> tileEntity, TileEntityRenderer renderer){
        this.rendererRegisterer.accept(tileEntity, renderer);
    }
}
