package net.xiaoyu233.fml.relaunch;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.MixinTransformerProxy;
import net.xiaoyu233.fml.classloading.LaunchClassLoader;
import net.xiaoyu233.fml.classloading.ModsWalker;
import net.xiaoyu233.fml.config.InjectionConfig;
import net.xiaoyu233.fml.mapping.Remapping;
import net.xiaoyu233.fml.mapping.Renamer;
import net.xiaoyu233.fml.mixin.service.ClassProvider;
import net.xiaoyu233.fml.util.LogProxy;
import net.xiaoyu233.fml.util.ModInfo;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class Launch {
   static {
      FishModLoader.loadConfig();

   }
   public static String mainClass;
   public static Map<String, Object> blackboard = new HashMap<>();
   public static LaunchClassLoader classLoader;
   public static String minecraftHome;

   public static void launch(String mainClass, String[] args) throws IOException, ClassNotFoundException {
      classLoader = new LaunchClassLoader(ClassProvider.getSystemClassPathURLs());
      seekGameDir(args);
      MixinBootstrap.init();
      onEnvironmentChanged();
      classLoader.registerTransformer(new Renamer(loadRemapping()));
      classLoader.registerTransformer(new MixinTransformerProxy(new MixinTransformer()));
      LogProxy.proxySysout();
      LogProxy.proxySyserr();
      FishModLoader.registerModloaderMixin(classLoader);
      URLClassLoader classLoader = (URLClassLoader) Launch.class.getClassLoader();
      ModsWalker modsWalker = new ModsWalker(FishModLoader.MOD_DIR, classLoader);
      for (InjectionConfig loadMod : modsWalker.loadMods(modsWalker.findMods((jarFile -> {
         try {
            URL url = jarFile.toURL();
            Launch.classLoader.addURL(url);
            ReflectHelper.addLoaderURL(classLoader,url);
            FishModLoader.LOGGER.info("Found mod jar:" + url.toString());
         } catch (MalformedURLException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
         }
      })), (abstractMod, dists) -> FishModLoader.addModInfo(new ModInfo(abstractMod.modId(), abstractMod.modVerStr(), abstractMod
              .modVerNum(), dists)))) {
         Mixins.registerConfiguration(loadMod.toConfig(Launch.classLoader, MixinService.getService(),MixinEnvironment.getCurrentEnvironment()));
      }

      try {
         MixinPlatformManager platform = MixinBootstrap.getPlatform();
         platform.init();
         Launch.classLoader.loadClass(mainClass);
         FishModLoader.LOGGER.info("Starting Minecraft");
         Class<?> var6 = Launch.classLoader.loadClass(mainClass);
         platform.inject();
         onEnvironmentChanged();
         var6.getMethod("main", String[].class).invoke(null, (Object) args);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException var11) {
         var11.printStackTrace();
      }

   }

   private static Remapping loadRemapping(){
      Remapping remapping = new Remapping();
      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/class.mapping"), Remapping.MappingType.CLASS);
      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/method.mapping"), Remapping.MappingType.METHOD);
      remapping.addMappingFromStream(Launch.class.getResourceAsStream("/field.mapping"), Remapping.MappingType.FIELD);
      MixinEnvironment.getCurrentEnvironment().getRemappers().add(remapping);
      return remapping;
   }

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
   }

}
