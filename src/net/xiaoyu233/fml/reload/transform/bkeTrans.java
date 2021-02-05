//package net.xiaoyu233.fml.reload.transform;
//
//import net.minecraft.bjo;
//import net.minecraft.bjp;
//import net.minecraft.bke;
//import net.xiaoyu233.fml.Translations;
//import net.xiaoyu233.fml.asm.annotations.Link;
//import net.xiaoyu233.fml.asm.annotations.Marker;
//import net.xiaoyu233.fml.asm.annotations.Transform;
//import net.xiaoyu233.fml.reload.event.LanguageResourceReloadEvent;
//import net.xiaoyu233.fml.reload.event.MITEEvents;
//
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//@Transform(bke.class)
//public class bkeTrans {
//   @Link
//   Map a;
//
//   public synchronized void a(bjp var1, List var2) {
//      this.a.clear();
//      Iterator var3 = var2.iterator();
//
//      while(var3.hasNext()) {
//         Object o = var3.next();
//         String var4 = (String)o;
//         String var5 = String.format("lang/%s.lang", var4);
//         MITEEvents.MITE_EVENT_BUS.post(new LanguageResourceReloadEvent(this.a, var4));
//         Translations.addTranslationsFor(this.a, var4);
//         Iterator var7 = var1.a().iterator();
//
//         while(var7.hasNext()) {
//            Object value = var7.next();
//            String var7 = (String)value;
//
//            try {
//               this.a(var1.b(new bjo(var7, var5)));
//            } catch (IOException var11) {
//            }
//         }
//      }
//
//      this.b();
//   }
//
//   @Marker
//   private void b() {
//   }
//
//   @Marker
//   private void a(List b) throws IOException {
//   }
//}
