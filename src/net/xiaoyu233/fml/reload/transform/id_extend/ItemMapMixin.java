package net.xiaoyu233.fml.reload.transform.id_extend;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.Entity;
import net.minecraft.ItemMap;
import net.minecraft.MapData;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemMap.class)
public class ItemMapMixin {
    @Inject(method = "updateMapData", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;getChunkFromBlockCoords(II)Lnet/minecraft/Chunk;"))
    private void mapFix(World world, Entity entity, MapData mapData, CallbackInfo callbackInfo, @Local LocalRef<int[]> var24){
        var24.set(new int[4096]);
    }
}
