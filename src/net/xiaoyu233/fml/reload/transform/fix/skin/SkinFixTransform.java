package net.xiaoyu233.fml.reload.transform.fix.skin;

import net.minecraft.AbstractTexture;
import net.minecraft.ResourceManager;
import net.minecraft.SimpleTexture;
import net.minecraft.ThreadDownloadImageData;
import net.xiaoyu233.fml.reload.utils.SkinDownloadThread;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ThreadDownloadImageData.class)
public abstract class SkinFixTransform extends AbstractTexture {
   @Shadow private Thread imageThread;
   @Shadow
   private SimpleTexture imageLocation;

   @Inject(method = "loadTexture", at = @At(value = "FIELD" ,target = "Lnet/minecraft/ThreadDownloadImageData;imageThread:Ljava/lang/Thread;", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
   private void injectReplaceCreateSkinThread(CallbackInfo callbackInfo){
      this.imageThread = new SkinDownloadThread(ReflectHelper.dyCast(this));
   }

   @Redirect(method = "loadTexture" ,at = @At(value = "INVOKE", target = "Lnet/minecraft/SimpleTexture;loadTexture(Lnet/minecraft/ResourceManager;)V"))
   private void redirectSafeSkinLoading(SimpleTexture obj, ResourceManager resourceManager){
      try {
         obj.loadTexture(resourceManager);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
