package net.xiaoyu233.fml.reload.event;

import net.minecraft.Entity;

public class EntityRegisterEvent {
    private final EntityRegisterer registerer;
    private final EntityRegistererWithEgg registererWithEgg;

    public EntityRegisterEvent(EntityRegisterer registerer, EntityRegistererWithEgg registererWithEgg) {
        this.registerer = registerer;
        this.registererWithEgg = registererWithEgg;
    }

    public void register(Class<? extends Entity> entityClass, String namespace, String name, int id) {
        this.registerer.register(entityClass, namespace, name, id);
    }

    public void register(Class<? extends Entity> entityClass, String namespace, String name, int id, int eggColorA, int eggColorB){
        this.registererWithEgg.registerWithEgg(entityClass, namespace, name, id, eggColorA, eggColorB);
    }

    public interface EntityRegisterer{
        void register(Class<? extends Entity> entityClass,String namespace, String name, int id);
    }

    public interface EntityRegistererWithEgg{
        void registerWithEgg(Class<? extends Entity> entityClass, String namespace, String name, int id, int eggColorA, int eggColorB);
    }
}
