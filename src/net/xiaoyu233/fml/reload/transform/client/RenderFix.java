package net.xiaoyu233.fml.reload.transform.client;

import net.minecraft.OpenGlHelper;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public class RenderFix {
    @Redirect(method = "setActiveTexture", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/ARBMultitexture;glActiveTextureARB(I)V"))
    private static void injectSetClientTextureARB(int textureId){
        ARBMultitexture.glClientActiveTextureARB(textureId);
    }

    @Inject(method = "setActiveTexture", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL13;glActiveTexture(I)V", shift = At.Shift.BEFORE))
    private static void injectSetClientTexture(int textureId, CallbackInfo callbackInfo){
        GL13.glClientActiveTexture(textureId);
    }
}
