package net.xiaoyu233.fml.mixin.service;

import net.xiaoyu233.fml.FishModLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.util.IConsumer;

import java.util.Collection;
import java.util.Collections;

public class PlatformAgent extends MixinPlatformAgentDefault implements IMixinPlatformServiceAgent {
   private static Logger log;
   private static AbstractAppender appender;
   private static Level oldLevel;

   public PlatformAgent() {

   }

   public AcceptResult accept(MixinPlatformManager manager, IContainerHandle handle) {
      this.manager = manager;
      this.handle = handle;
      return AcceptResult.ACCEPTED;
   }

   public String getPhaseProvider() {
      return null;
   }

   public void initPrimaryContainer() {
      this.injectRemapper();
   }

   private void injectRemapper() {
   }

   public void inject() {
   }

   public void init() {
   }

   public String getSideName() {
      return FishModLoader.isServer() ? "SERVER" : "CLIENT";
   }

   public Collection<IContainerHandle> getMixinContainers() {
      return Collections.singleton(this.handle);
   }

   public void wire(Phase phase, IConsumer<Phase> phaseConsumer) {
      super.wire(phase, phaseConsumer);
      if (phase == Phase.PREINIT) {
         begin(phaseConsumer);
      }

   }

   static void begin(IConsumer<Phase> delegate) {
      org.apache.logging.log4j.core.Logger fmlLog = (Logger) LogManager.getLogger("FishModLoader");
      if (fmlLog instanceof Logger) {
         log = fmlLog;
         oldLevel = log.getLevel();
         appender = new PlatformAgent.MixinAppender(delegate);
         appender.start();
         log.addAppender(appender);
         log.setLevel(Level.ALL);
      }
   }

   public void unwire() {
      end();
   }

   static void end() {
      if (log != null) {
         log.removeAppender(appender);
      }

   }

   static class MixinAppender extends AbstractAppender {
      private final IConsumer<Phase> delegate;

      MixinAppender(IConsumer<Phase> delegate) {
         super("MixinLogWatcherAppender", null, null);
         this.delegate = delegate;
      }

      public void append(LogEvent event) {
         if ("Starting Minecraft".equals(event.getMessage().getFormattedMessage())) {
            this.delegate.accept(Phase.INIT);
            if (PlatformAgent.log.getLevel() == Level.ALL) {
               PlatformAgent.log.setLevel(PlatformAgent.oldLevel);
            }

         }
      }
   }
}
