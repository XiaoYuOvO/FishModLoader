package net.xiaoyu233.fml.reload.transform.client;

import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.ModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.InputStream;
import java.util.List;

@Mixin(SimpleReloadableResourceManager.class)
public class SimpleResourceManagerMixin {
    @Redirect(method = "getAllResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/ResourceManager;getAllResources(Lnet/minecraft/ResourceLocation;)Ljava/util/List;"))
    private List enhanceGetAllResources(ResourceManager obj, ResourceLocation location){
        List allResources = obj.getAllResources(location);
        for (ModInfo value : FishModLoader.getModsMap().values()) {
            MetadataSerializer metadataSerializer = new MetadataSerializer();
            InputStream resourceAsStream = value.getMod().getClass().getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
            InputStream metaStream = value.getMod().getClass().getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath() + ".mcmeta");
            if (resourceAsStream != null){
                allResources.add(new SimpleResource(location, resourceAsStream, metaStream, metadataSerializer));
            }
        }
        return allResources;
    }
}
