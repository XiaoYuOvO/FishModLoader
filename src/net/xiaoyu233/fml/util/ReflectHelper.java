package net.xiaoyu233.fml.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class ReflectHelper {


   public static Class<?> reloadClassWithLoader(Class<?> className,ClassLoader classLoader) throws ClassNotFoundException {
       return classLoader.loadClass(className.getName());
   }

   public static void updateFinalModifiers(Field field) throws NoSuchFieldException, IllegalAccessException {
      field.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
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
