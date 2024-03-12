package net.xiaoyu233.fml.reload.transform.client;

import net.fabricmc.loader.impl.ModContainerImpl;
import net.minecraft.MetadataSerializer;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.SimpleReloadableResourceManager;
import net.xiaoyu233.fml.FishModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(SimpleReloadableResourceManager.class)
public class SimpleResourceManagerMixin {
    @Redirect(method = "getAllResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/ResourceManager;getAllResources(Lnet/minecraft/ResourceLocation;)Ljava/util/List;"))
    private List enhanceGetAllResources(ResourceManager obj, ResourceLocation location){
        List allResources = obj.getAllResources(location);
        for (ModContainerImpl value : FishModLoader.getModsMap().values()) {
            MetadataSerializer metadataSerializer = new MetadataSerializer();
//            InputStream resourceAsStream = value.getP.getClass().getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
//            InputStream metaStream = value.getMod().getClass().getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath() + ".mcmeta");
//            if (resourceAsStream != null){
//                allResources.add(new SimpleResource(location, resourceAsStream, metaStream, metadataSerializer));
//            }
        }
        return allResources;
    }
}
