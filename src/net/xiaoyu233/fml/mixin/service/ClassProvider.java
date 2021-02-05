package net.xiaoyu233.fml.mixin.service;

import net.xiaoyu233.fml.relaunch.Launch;
import org.spongepowered.asm.service.IClassProvider;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class ClassProvider implements IClassProvider {
   ClassProvider() {
   }

   public static URL[] getSystemClassPathURLs() {
      ClassLoader classLoader = ClassProvider.class.getClassLoader();
      if (classLoader instanceof URLClassLoader) {
         return ((URLClassLoader)classLoader).getURLs();
      } else if (classLoader.getClass().getName().startsWith("jdk.internal.loader.ClassLoaders$")) {
         try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe)field.get(null);
            Field ucpField = classLoader.getClass().getDeclaredField("ucp");
            long ucpFieldOffset = unsafe.objectFieldOffset(ucpField);
            Object ucpObject = unsafe.getObject(classLoader, ucpFieldOffset);
            Field pathField = ucpField.getType().getDeclaredField("path");
            long pathFieldOffset = unsafe.objectFieldOffset(pathField);
            ArrayList<URL> path = (ArrayList)unsafe.getObject(ucpObject, pathFieldOffset);
            return path.toArray(new URL[0]);
         } catch (Exception var11) {
            var11.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   public URL[] getClassPath() {
      return getSystemClassPathURLs();
   }

   public Class<?> findClass(String name) throws ClassNotFoundException {
      return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
   }

   public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
      return Class.forName(name, initialize, Thread.currentThread().getContextClassLoader());
   }

   public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
      return Class.forName(name, initialize, Launch.class.getClassLoader());
   }
}
