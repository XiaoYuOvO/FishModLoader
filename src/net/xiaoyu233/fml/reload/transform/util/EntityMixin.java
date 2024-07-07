package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Entity;
import net.xiaoyu233.fml.api.entity.IEntity;
import net.xiaoyu233.fml.util.WriteLockField;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {
    private final WriteLockField<String> entityNamespace = WriteLockField.create("Minecraft");
    public String getNamespace(){return entityNamespace.get();}

    public void setNamespace(String namespace){entityNamespace.set(namespace);};

    public boolean hasNamespaceSet(){return entityNamespace.isLocked();}
}
