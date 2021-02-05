package net.xiaoyu233.fml.mixin.service;

import org.spongepowered.asm.service.IMixinAuditTrail;

import java.util.function.Consumer;

public class MixinAuditTrail implements IMixinAuditTrail {
   private static final String APPLY_MIXIN_ACTIVITY = "APP";
   private static final String POST_PROCESS_ACTIVITY = "DEC";
   private static final String GENERATE_ACTIVITY = "GEN";
   private String currentClass;
   private Consumer<String[]> consumer;

   public void setConsumer(String className, Consumer<String[]> consumer) {
      this.currentClass = className;
      this.consumer = consumer;
   }

   public void onApply(String className, String mixinName) {
      this.writeActivity(className, "APP", mixinName);
   }

   public void onPostProcess(String className) {
      this.writeActivity(className, "DEC");
   }

   public void onGenerate(String className, String generatorName) {
      this.writeActivity(className, "GEN");
   }

   private void writeActivity(String className, String... activity) {
      if (this.consumer != null && className.equals(this.currentClass)) {
         this.consumer.accept(activity);
      }

   }
}
