package net.xiaoyu233.fml.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;

public class LogProxy extends PrintStream {
   public static final Logger logger = LogManager.getLogger("STDOUT");
   private final LogProxy.ProxyStyle style;

   public LogProxy(OutputStream out, LogProxy.ProxyStyle style) {
      super(out);
      this.style = style;
   }

   public void print(String s) {
      this.style.loggerMsgConsumer.accept(logger, s);
   }

   public void println(String x) {
      this.style.loggerMsgConsumer.accept(logger, x);
   }

   public void println() {
      this.style.loggerMsgConsumer.accept(logger, "");
   }

   public static void proxySysout() {
      try {
         Field out = System.class.getField("out");
         out.setAccessible(true);
         Field modifiers = out.getClass().getDeclaredField("modifiers");
         modifiers.setAccessible(true);
         modifiers.setInt(out, out.getModifiers() & -17);
         out.set(null, new LogProxy(System.out, LogProxy.ProxyStyle.OUT));
         modifiers.setInt(out, out.getModifiers() & -17);
      } catch (IllegalAccessException | NoSuchFieldException var2) {
         var2.printStackTrace();
      }

   }

   public static void proxySyserr() {
      try {
         Field out = System.class.getField("err");
         out.setAccessible(true);
         Field modifiers = out.getClass().getDeclaredField("modifiers");
         modifiers.setAccessible(true);
         modifiers.setInt(out, out.getModifiers() & -17);
         modifiers.setInt(out, out.getModifiers() & -17);
      } catch (IllegalAccessException | NoSuchFieldException var2) {
         var2.printStackTrace();
      }

   }

   private enum ProxyStyle {
      OUT(Logger::info),
      ERR(Logger::error);

      private final BiConsumer<Logger, String> loggerMsgConsumer;

      ProxyStyle(BiConsumer<Logger, String> loggerMsgConsumer) {
         this.loggerMsgConsumer = loggerMsgConsumer;
      }
   }
}
