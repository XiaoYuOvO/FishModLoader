package net.xiaoyu233.fml.relaunch;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FormattedException;
import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.TinyUtils;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.classloading.KnotClassLoaderInterface;
import net.xiaoyu233.fml.mapping.CachedMappedJar;
import net.xiaoyu233.fml.util.EnumExtends;
import net.xiaoyu233.fml.util.LogProxy;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Launch {
   public static Map<String, Object> blackboard = new HashMap<>();
   public static String minecraftHome;
   public static KnotClassLoaderInterface knotLoader;
   public static Arguments arguments;

   //All these things are happened in AppClassLoader, REMEMBER THIS!
   public static void launch(KnotClassLoaderInterface knotInterface, String mainClass, String[] args, boolean server, Path gameJarPath) throws IOException, ClassNotFoundException, NoSuchMethodException, ModResolutionException {
      knotLoader = knotInterface;
      arguments = new Arguments();
      arguments.parse(args);
      seekGameDir(args);
      //Use parent to prevent preloading
      Path remappedGameJarPath;
      try {
         IMappingProvider tinyMappingProvider = TinyUtils.createTinyMappingProvider(new BufferedReader(new InputStreamReader(Objects.requireNonNull(Launch.class.getResourceAsStream("/mappings.tiny")))), "official", "named");
         CachedMappedJar cachedMappedJar = new CachedMappedJar(gameJarPath, tinyMappingProvider, new File(minecraftHome));
         remappedGameJarPath = cachedMappedJar.ensureJarMapped();
         knotInterface.addCodeSource(remappedGameJarPath);
      }catch (Exception e){
         throw new RuntimeException("Cannot remap minecraft jar", e);
      }

      ClassLoader knotLoader = knotInterface.getClassLoader();
      Thread.currentThread().setContextClassLoader(knotLoader);
      FishModLoader.setup(remappedGameJarPath);
      onEnvironmentChanged();
      FishModLoader.freeze();
      FishModLoader.loadAccessWideners();
      FishModLoader.initModMixin();
      LogProxy.proxySysout();
      try {
         MixinPlatformManager platform = MixinBootstrap.getPlatform();
         platform.init();
         FishModLoader.LOGGER.info("Starting Minecraft");
         platform.inject();
         knotInterface.initializeTransformers();
         MixinExtrasBootstrap.init();
         onEnvironmentChanged();
         try {
            FishModLoader.invokeEntrypoints("preLaunch", PreLaunchEntrypoint.class, PreLaunchEntrypoint::onPreLaunch);
            EnumExtends.buildEnumExtending();
         } catch (RuntimeException e) {
            throw FormattedException.ofLocalized("exception.initializerFailure", e);
         }
         knotLoader.loadClass(mainClass).getMethod("main", String[].class).invoke(null, (Object) args);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
         FishModLoader.LOGGER.error("Cannot launch minecraft", e);
      }

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
      if (minecraftHome == null){
         minecraftHome = System.getProperty("user.dir");
      }
   }

}
