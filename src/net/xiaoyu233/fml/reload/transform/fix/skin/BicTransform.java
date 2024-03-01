package net.xiaoyu233.fml.reload.transform.fix.skin;

import net.minecraft.bia;
import net.minecraft.bic;
import net.minecraft.bif;
import net.minecraft.bjp;
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

@Mixin(bic.class)
public abstract class BicTransform extends bia {
   @Shadow private Thread e;
   @Shadow
   private bif f;

   @Inject(method = "a(Lnet/minecraft/bjp;)V", at = @At(value = "FIELD" ,target = "Lnet/minecraft/bic;e:Ljava/lang/Thread;", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
   private void injectReplaceCreateSkinThread(CallbackInfo callbackInfo){
      this.e = new SkinDownloadThread(ReflectHelper.dyCast(this));
   }

   @Redirect(method = "a(Lnet/minecraft/bjp;)V" ,at = @At(value = "INVOKE", target = "Lnet/minecraft/bif;a(Lnet/minecraft/bjp;)V"))
   private void redirectSafeSkinLoading(bif obj, bjp resourceManager){
      try {
         this.f.a(resourceManager);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Shadow
   public abstract int b();
}
