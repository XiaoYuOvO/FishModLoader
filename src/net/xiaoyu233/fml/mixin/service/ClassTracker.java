package net.xiaoyu233.fml.mixin.service;

import org.spongepowered.asm.service.IClassTracker;

import java.util.*;

public class ClassTracker implements IClassTracker {
   private final List<String> invalidClasses = new ArrayList();
   private final Map<String, String> cachedClasses = new HashMap();
   private Set<String> classLoaderExceptions;
   private Set<String> transformerExceptions;

   public boolean isClassLoaded(String name) {
      return this.cachedClasses.containsKey(name);
   }

   public String getClassRestrictions(String className) {
      String restrictions = "";
      if (this.isClassClassLoaderExcluded(className, null)) {
         restrictions = "PACKAGE_CLASSLOADER_EXCLUSION";
      }

      if (this.isClassTransformerExcluded(className, null)) {
         restrictions = (restrictions.length() > 0 ? restrictions + "," : "") + "PACKAGE_TRANSFORMER_EXCLUSION";
      }

      return restrictions;
   }

   boolean isClassExcluded(String name, String transformedName) {
      return this.isClassClassLoaderExcluded(name, transformedName) || this.isClassTransformerExcluded(name, transformedName);
   }

   boolean isClassClassLoaderExcluded(String name, String transformedName) {
      Iterator var3 = this.getClassLoaderExceptions().iterator();

      String exception;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         exception = (String)var3.next();
      } while((transformedName == null || !transformedName.startsWith(exception)) && !name.startsWith(exception));

      return true;
   }

   Set<String> getClassLoaderExceptions() {
      return this.classLoaderExceptions != null ? this.classLoaderExceptions : Collections.emptySet();
   }

   boolean isClassTransformerExcluded(String name, String transformedName) {
      Iterator var3 = this.getTransformerExceptions().iterator();

      String exception;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         exception = (String)var3.next();
      } while((transformedName == null || !transformedName.startsWith(exception)) && !name.startsWith(exception));

      return true;
   }

   Set<String> getTransformerExceptions() {
      return this.transformerExceptions != null ? this.transformerExceptions : Collections.emptySet();
   }

   public void registerInvalidClass(String name) {
      if (this.invalidClasses != null) {
         this.invalidClasses.add(name);
      }

   }
}
