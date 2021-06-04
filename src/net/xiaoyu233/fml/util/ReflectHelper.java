package net.xiaoyu233.fml.util;

import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;

public class ReflectHelper {
   private static final Class<? extends URLClassLoader> URLClassLoader = URLClassLoader.class;
   private static Method addURL;

   static {
      try {
         addURL = URLClassLoader.getDeclaredMethod("addURL", URL.class);
         addURL.setAccessible(true);
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      }
   }

   public static Class<?> reloadClassWithLoader(Class<?> className,ClassLoader classLoader) throws ClassNotFoundException {
       return classLoader.loadClass(className.getName());
   }

   public static void updateFinalModifiers(Field field) throws NoSuchFieldException, IllegalAccessException {
      field.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
   }

   public static void addLoaderURL(URLClassLoader loader, URL url) throws InvocationTargetException, IllegalAccessException {
      addURL.invoke(loader,url);
   }
   public static <T> T dyCast(Object from) {
      return (T) from;
   }

   public static <T> T dyCast(Class<T> to, Object from) {
      return (T) from;
   }

   public static <T> T createInstance(Class<T> tClass, Object... args) {
      Class<?>[] types = new Class[args.length];

      for(int i = 0; i < args.length; ++i) {
         types[i] = args[i].getClass();
      }

      try {
         Constructor<T> ctor = tClass.getDeclaredConstructor(types);
         ctor.setAccessible(true);
         return ctor.newInstance(args);
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static <T> T createInstance(Class<T> tClass, Class[] types, Object... args) {
      try {
         Constructor<T> ctor = tClass.getDeclaredConstructor(types);
         ctor.setAccessible(true);
         return ctor.newInstance(args);
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException var4) {
         var4.printStackTrace();
         return null;
      }
   }
}
