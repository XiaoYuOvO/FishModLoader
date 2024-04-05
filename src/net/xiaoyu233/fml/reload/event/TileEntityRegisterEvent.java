package net.xiaoyu233.fml.reload.event;

import net.minecraft.TileEntity;

public class TileEntityRegisterEvent {
    private final TileEntityRegisterer registerer;

    public TileEntityRegisterEvent(TileEntityRegisterer registerer) {
        this.registerer = registerer;
    }

    public <E extends TileEntity> void register(Class<E> clazz, String id){
        registerer.register(clazz,id);
    }

    public interface TileEntityRegisterer{
        <E extends TileEntity> void register(Class<E> clazz, String id);
    }
}
