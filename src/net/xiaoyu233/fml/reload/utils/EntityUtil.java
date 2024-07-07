package net.xiaoyu233.fml.reload.utils;

import net.minecraft.Entity;
import net.xiaoyu233.fml.util.WriteLockField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class EntityUtil {
    private static final WriteLockField<String> MINECRAFT = WriteLockField.createLocked("Minecraft");
    @Nullable
    private static Map<Class<? extends Entity>, WriteLockField<String>> entityNamespaceMap;

    @Nonnull
    public static String getNamespace(Class<? extends Entity> entityClass){
        if (entityNamespaceMap != null){
            return entityNamespaceMap.getOrDefault(entityClass, MINECRAFT).get();
        }
        throw new IllegalStateException("Entities are not registered now");
    }

    public static void initEntityNamespaceMap(Map<Class<? extends Entity>, WriteLockField<String>> namespaceMap){
        if (entityNamespaceMap == null){
            entityNamespaceMap = namespaceMap;
        }else {
            throw new IllegalStateException("Entities are already registered");
        }
    }
}
