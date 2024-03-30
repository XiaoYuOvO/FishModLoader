package net.xiaoyu233.fml.reload.transform.save_version;

import net.minecraft.NBTTagCompound;
import net.minecraft.WorldInfoShared;
import net.minecraft.WorldSettings;
import net.xiaoyu233.fml.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldInfoShared.class)
public class WorldInfoMixin {
    public int fmlSaveVersion;

    @Inject(method = "<init>(Lnet/minecraft/NBTTagCompound;)V", at= @At("RETURN"))
    private void injectReadInfo(NBTTagCompound compound, CallbackInfo callbackInfo){
        if (compound.hasKey("fml_version")){
            fmlSaveVersion = compound.getInteger("fml_version");
        }else {
            fmlSaveVersion = Constants.FML_SAVE_VERSION;
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/WorldSettings;Ljava/lang/String;)V", at = @At("RETURN"))
    private void injectDefaultVersion(WorldSettings world_settings, String level_name, CallbackInfo callbackInfo){
        this.fmlSaveVersion = Constants.FML_SAVE_VERSION;
    }


    @Inject(method = "updateTagCompound", at = @At("RETURN"))
    private void injectUpdateTagCompound(NBTTagCompound oldCompound, NBTTagCompound playerCompound, CallbackInfo callbackInfo){
        oldCompound.setInteger("fml_version", this.fmlSaveVersion);
    }
}
