package net.xiaoyu233.fml.reload.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;

public class MITEEvents {
   public static final SubscriberExceptionHandler handler = (exception, context) -> {
      System.err.println("Error handling event:" + context.getEvent().getClass().getSimpleName() + " for handler: " + context.getSubscriber().getClass().getName() + "." + context.getSubscriberMethod().getName());
      exception.printStackTrace();
   };
   public static final EventBus MITE_EVENT_BUS;

   static {
      MITE_EVENT_BUS = new EventBus(handler);
   }
}
