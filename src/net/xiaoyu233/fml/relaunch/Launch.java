package net.xiaoyu233.fml.relaunch;

import com.google.common.collect.Lists;
import net.xiaoyu233.fml.AbstractMod;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.MixinTransformerProxy;
import net.xiaoyu233.fml.classloading.LaunchClassLoader;
import net.xiaoyu233.fml.classloading.ModsWalker;
import net.xiaoyu233.fml.config.InjectionConfig;
import net.xiaoyu233.fml.mapping.CachedMappedJar;
import net.xiaoyu233.fml.mixin.service.ClassProvider;
import net.xiaoyu233.fml.util.LogProxy;
import net.xiaoyu233.fml.util.ModInfo;
import net.xiaoyu233.fml.util.ReflectHelper;
import net.xiaoyu233.fml.util.UIUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.service.MixinService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("DanglingJavadoc")
public class Launch {
   public static String mainClass;
   public static Map<String, Object> blackboard = new HashMap<>();
   public static LaunchClassLoader transformClassLoader;
   public static String minecraftHome;
   public static void launch(String mainClass, String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException {
      FishModLoader.loadConfig();
      URLClassLoader nativeClassLoader = (URLClassLoader) Launch.class.getClassLoader();
      Class<?> nativeMainClass = nativeClassLoader.loadClass(mainClass);
      URL minecraftSourceJar = nativeMainClass.getProtectionDomain().getCodeSource().getLocation();
      transformClassLoader = new LaunchClassLoader(Arrays.stream(Objects.requireNonNull(ClassProvider.getSystemClassPathURLs())).filter(url -> !url.getPath().equals(minecraftSourceJar.getPath())).collect(Collectors.toList()).toArray(new URL[]{}));
      seekGameDir(args);
      MixinBootstrap.init();
      onEnvironmentChanged();
//      transformClassLoader.registerTransformer(new Renamer(loadRemapping()));
      transformClassLoader.registerTransformer(new MixinTransformerProxy(new MixinTransformer()));
      LogProxy.proxySysout();
      LogProxy.proxySyserr();
      FishModLoader.registerModloaderMixin(transformClassLoader);
      ModsWalker modsWalker = new ModsWalker(FishModLoader.MOD_DIR, nativeClassLoader);
      for (InjectionConfig loadMod : modsWalker.loadMods(modsWalker.findMods(jarFile -> addModToLoader(nativeClassLoader, jarFile)), Launch::modRegister)) {
         Mixins.registerConfiguration(loadMod.toConfig(Launch.transformClassLoader, MixinService.getService(),MixinEnvironment.getCurrentEnvironment()));
      }

      try {
         MixinPlatformManager platform = MixinBootstrap.getPlatform();
         platform.init();
//         Launch.transformClassLoader.loadClass(mainClass);
         //Remap minecraft jars
         CachedMappedJar cachedMappedJar = new CachedMappedJar(minecraftSourceJar, new BufferedReader(new InputStreamReader(Objects.requireNonNull(Launch.class.getResourceAsStream("/mappings.tiny")))), new File(minecraftHome));
         Launch.transformClassLoader.addURL(cachedMappedJar.ensureJarMapped());

         //Finish remap minecraft jars
         FishModLoader.LOGGER.info("Starting Minecraft");
         platform.inject();
         onEnvironmentChanged();
         Class<?> modInfo = ReflectHelper.reloadClassWithLoader(ModInfo.class,Launch.transformClassLoader);
         Class<?> absModInfo = ReflectHelper.reloadClassWithLoader(AbstractMod.class,Launch.transformClassLoader);
         Class<?> fmlClass = ReflectHelper.reloadClassWithLoader(FishModLoader.class, Launch.transformClassLoader);
         Method addModInfo = fmlClass.getDeclaredMethod("addModInfo", modInfo);
         for (ModInfo value : FishModLoader.getModsMap().values()) {
            Class<?> aClass = Launch.transformClassLoader.loadClass(value.getMod().getClass().getName());
            try {
               Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
               declaredConstructor.setAccessible(true);
               Object o = declaredConstructor.newInstance();
               /**
                * @see AbstractMod#postInit()
                * */
               aClass.getMethod("postInit").invoke(o);
               /**
                * @see FishModLoader#reloadAllConfigs()
                * */
               fmlClass.getMethod("reloadAllConfigs").invoke(null);
               try {
                  /**
                   * @see FishModLoader#addModInfo(ModInfo)
                   * */
                  addModInfo.invoke(null, modInfo.getConstructor(absModInfo, List.class).newInstance(o, value.getDists()));
               } catch (Exception e) {
                  FishModLoader.LOGGER.error("Cannot add mod info " + value.getModid() + "-" + value.getModVerStr() + " quitting!", e);
                  System.exit(-1);
               }
            }catch (Exception e){
               FishModLoader.LOGGER.error("Cannot run post init for " + value.getModid(),e);
            }
         }
         transformClassLoader.loadClass(mainClass).getMethod("main", String[].class).invoke(null, (Object) args);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException var11) {
         var11.printStackTrace();
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      }

   }

   private static void addModToLoader(URLClassLoader nativeClassLoader, File jarFile) {
      try {
         URL url = jarFile.toURL();
         Launch.transformClassLoader.addURL(url);
         ReflectHelper.addLoaderURL(nativeClassLoader,url);
         FishModLoader.LOGGER.info("Found mod jar:" + url.toString());
      } catch (MalformedURLException | IllegalAccessException | InvocationTargetException e) {
         e.printStackTrace();
      }
   }

//   private static Remapping loadRemapping(){
//      Remapping remapping = new Remapping();
//      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/class.mapping"), Remapping.MappingType.CLASS);
//      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/method.mapping"), Remapping.MappingType.METHOD);
//      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/field.mapping"), Remapping.MappingType.FIELD);
//      MixinEnvironment.getCurrentEnvironment().getRemappers().add(remapping);
//      return remapping;
//   }

   public static void onEnvironmentChanged(){
      MixinEnvironment currentEnvironment = MixinEnvironment.getCurrentEnvironment();
      currentEnvironment.setSide(FishModLoader.getSide());
      currentEnvironment.setOption(MixinEnvironment.Option.DEBUG_VERBOSE,true);
   }

   private static void seekGameDir(String[] args){
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.contains("gameDir")) {
            String gamePath = args[i + 1];
            System.out.println(gamePath);
            System.setProperty("user.dir", gamePath);
            minecraftHome = gamePath;
         }
      }
      if (minecraftHome == null){
         minecraftHome = System.getProperty("user.dir");
      }
   }

   private static void modRegister(AbstractMod abstractMod, MixinEnvironment.Side[] dists) {
      if (!FishModLoader.hasMod(abstractMod.modId())) {
         abstractMod.preInit();
         FishModLoader.addModInfo(new ModInfo(abstractMod, Lists.newArrayList(dists)));
      } else {
         if (!FishModLoader.isServer()) {
            UIUtils.showErrorDialog("错误,模组重复!游戏即将退出\n" + abstractMod.modId() + "-" + abstractMod.modVerStr());
            System.exit(1);
         }
         throw new IllegalArgumentException("Duplicated mods! (重复的模组)" + abstractMod.modId() + "-" + abstractMod.modVerStr());
      }
   }
}
