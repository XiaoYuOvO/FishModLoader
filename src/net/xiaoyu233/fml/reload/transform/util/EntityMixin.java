package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Entity;
import net.xiaoyu233.fml.api.entity.IEntity;
import net.xiaoyu233.fml.reload.utils.EntityUtil;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {

    public String getNamespace(){return EntityUtil.getNamespace(ReflectHelper.dyCast(Entity.class, this).getClass());}

    public void setNamespace(String namespace){ throw new UnsupportedOperationException();}

    public boolean hasNamespaceSet(){return true;}
}
