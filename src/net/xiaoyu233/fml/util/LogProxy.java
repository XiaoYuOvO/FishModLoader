package net.xiaoyu233.fml.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;
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
       System.setOut(new LogProxy(System.out, ProxyStyle.OUT));

   }

   public static void proxySyserr() {
      System.setErr(new LogProxy(System.err, ProxyStyle.ERR));
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
