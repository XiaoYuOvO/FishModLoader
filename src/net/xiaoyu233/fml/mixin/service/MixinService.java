package net.xiaoyu233.fml.mixin.service;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import net.xiaoyu233.fml.asm.IClassNameTransformer;
import net.xiaoyu233.fml.asm.IClassTransformer;
import net.xiaoyu233.fml.relaunch.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.perf.Profiler;
import org.spongepowered.asm.util.perf.Profiler.Section;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class MixinService extends MixinServiceAbstract implements IClassBytecodeProvider, ITransformerProvider {
   private IClassProvider classProvider;
   private ClassTracker classTracker;
   private ContainerHandle containerHandle;
   private IMixinAuditTrail auditTrail;
   private List<ILegacyClassTransformer> legacyTransformers;
   private IMixinPlatformServiceAgent platformServiceAgent;

   public MixinService(){
   }

   private byte[] applyTransformers(String name, String transformedName, byte[] basicClass, Profiler profiler) {
      if (!this.classTracker.isClassExcluded(name, transformedName)) {

         for (ILegacyClassTransformer transformer : this.getDelegatedLegacyTransformers()) {
            this.lock.clear();
            int pos = transformer.getName().lastIndexOf(46);
            String simpleName = transformer.getName().substring(pos + 1);
            Section transformTime = profiler.begin(2, simpleName.toLowerCase(Locale.ROOT));
            transformTime.setInfo(transformer.getName());
            basicClass = transformer.transformClassBytes(name, transformedName, basicClass);
            transformTime.end();
            if (this.lock.isSet()) {
//               this.addTransformerExclusion(transformer.getName());
               this.lock.clear();
               MixinServiceAbstract.logger.info("A re-entrant transformer '{}' was detected and will no longer process meta class data", transformer
                       .getName());
            }
         }

      }
      return basicClass;
   }

   public Collection<ITransformer> getDelegatedTransformers() {
      return Collections.unmodifiableList(this.getDelegatedLegacyTransformers());
   }

   private void checkContainer(){
      if (this.containerHandle == null) {
         this.containerHandle = new ContainerHandle("FML");
      }
   }

   public void addTransformerExclusion(String name) {
   }

   public String getName() {
      return "FishModLoaderService";
   }

   public CompatibilityLevel getMinCompatibilityLevel() {
      return CompatibilityLevel.JAVA_8;
   }

   public boolean isValid() {
      return true;
   }

   public IClassProvider getClassProvider() {
      if (this.classProvider == null) {
         this.classProvider = new ClassProvider();
      }

      return this.classProvider;
   }

   public IClassBytecodeProvider getBytecodeProvider() {
      return this;
   }

   public ITransformerProvider getTransformerProvider() {
      return this;
   }

   public IClassTracker getClassTracker() {
      if (this.classTracker == null) {
         this.classTracker = new ClassTracker();
      }

      return this.classTracker;
   }

   public IMixinAuditTrail getAuditTrail() {
      if (this.auditTrail == null) {
         this.auditTrail = new MixinAuditTrail();
      }

      return this.auditTrail;
   }

   public byte[] getClassBytes(String name, String transformedName) throws IOException {
      byte[] classBytes = Launch.classLoader.getClassBytes(name);
      if (classBytes != null) {
         return classBytes;
      } else {
         URLClassLoader appClassLoader;
         if (Launch.class.getClassLoader() instanceof URLClassLoader) {
            appClassLoader = (URLClassLoader)Launch.class.getClassLoader();
         } else {
            appClassLoader = new URLClassLoader(new URL[0], Launch.class.getClassLoader());
         }

         InputStream classStream = null;

         try {
            String resourcePath = transformedName.replace('.', '/').concat(".class");
            classStream = appClassLoader.getResourceAsStream(resourcePath);
            return ByteStreams.toByteArray(classStream);
         } catch (Exception ignored) {
         } finally {
            Closeables.closeQuietly(classStream);
         }

         return null;
      }
   }

   public List<ILegacyClassTransformer> getDelegatedLegacyTransformers() {
      return Lists.newArrayList(Launch.classLoader.getRenameTransformer());
   }

   @Override
   public Collection<IContainerHandle> getMixinContainers() {
      this.checkContainer();
      return Collections.singleton(this.containerHandle);
   }

   public InputStream getResourceAsStream(String name) {
      return Launch.class.getResourceAsStream(name);
   }

   public ClassNode getClassNode(String className) throws ClassNotFoundException, IOException {
      return this.getClassNode(this.getClassBytes(className, true), 8);
   }

   public ClassNode getClassNode(String className, boolean runTransformers) throws ClassNotFoundException, IOException {
      return this.getClassNode(this.getClassBytes(className, true), 8);
   }

   private ClassNode getClassNode(byte[] classBytes, int flags) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(classBytes);
      classReader.accept(classNode, flags);
      return classNode;
   }

   public byte[] getClassBytes(String className, boolean runTransformers) throws ClassNotFoundException, IOException {
      String name = Launch.classLoader.untransformName(className);
      Profiler profiler = MixinEnvironment.getProfiler();
      Section loadTime = profiler.begin(1, "class.load");
      byte[] classBytes = this.getClassBytes(className, className);
      if (name != null) {
         classBytes = this.getClassBytes(name, className);
      }

      loadTime.end();
      if (runTransformers) {
         Section transformTime = profiler.begin(1, "class.transform");
         classBytes = this.applyTransformers(name, className, classBytes, profiler);
         transformTime.end();
      }

      if (classBytes == null) {
         throw new ClassNotFoundException(String.format("The specified class '%s' was not found", className));
      } else {
         return classBytes;
      }
   }

   public Collection<String> getPlatformAgents() {
      if (this.platformServiceAgent == null) {
         this.platformServiceAgent = new PlatformAgent();
      }

      return Lists.newArrayList(this.platformServiceAgent.getClass().getName());
   }

   public IContainerHandle getPrimaryContainer() {
      this.checkContainer();

      return this.containerHandle;
   }

   public Collection<ITransformer> getTransformers() {
      List<IClassTransformer> transformers = Launch.classLoader.getTransformers();
      List<ITransformer> wrapped = new ArrayList<>(transformers.size());
      MixinTransformer activeTransformer = (MixinTransformer)MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
      MixinTransformer mixin;
      if (activeTransformer == null) {
         mixin = new MixinTransformer();
      } else{
         mixin = activeTransformer;
      }
      wrapped.add(mixin);
      for (IClassTransformer transformer : transformers) {
         if (transformer instanceof ITransformer) {
            wrapped.add((ITransformer) transformer);
         } else {
            wrapped.add(new TransformerWrapper(transformer));
         }

         if (transformer instanceof IClassNameTransformer) {
            MixinServiceAbstract.logger.debug("Found name transformer: {}", transformer.getClass().getName());
         }
      }

      return wrapped;
   }
}
