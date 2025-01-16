package net.xiaoyu233.fml.relaunch;

import com.chocohead.mm.AsmTransformer;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FormattedException;
import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
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

import java.io.*;
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
      setupUncaughtExceptionHandler();
      knotLoader = knotInterface;
      arguments = new Arguments();
      arguments.parse(args);
      FishModLoader.setIsServer(server);
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
         AsmTransformer asmTransformer = new AsmTransformer();
         knotInterface.initializeTransformers(asmTransformer);
         MixinExtrasBootstrap.init();
         onEnvironmentChanged();
         try {
            FishModLoader.invokeEntrypoints("preLaunch", PreLaunchEntrypoint.class, PreLaunchEntrypoint::onPreLaunch);
            EnumExtends.buildEnumExtending();
            asmTransformer.buildAndInitializeTransformer(knotInterface::addUrl);
         } catch (RuntimeException e) {
            throw FormattedException.ofLocalized("exception.initializerFailure", e);
         }
         knotLoader.loadClass(mainClass).getMethod("main", String[].class).invoke(null, (Object) args);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
         FishModLoader.LOGGER.error("Cannot launch minecraft", e);
      }

   }

   protected static void handleFormattedException(FormattedException exc) {
      Throwable actualExc = exc.getMessage() != null ? exc : exc.getCause();
      Log.error(LogCategory.GENERAL, exc.getMainText(), actualExc);
      FabricGuiEntry.displayError(exc.getDisplayedText(), actualExc, true);
      throw new AssertionError("exited");
   }

   protected static void setupUncaughtExceptionHandler() {
      Thread mainThread = Thread.currentThread();
      Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
         try {
            if (e instanceof FormattedException) {
               handleFormattedException((FormattedException) e);
            } else {
               String mainText = String.format("Uncaught exception in thread \"%s\"", t.getName());
               Log.error(LogCategory.GENERAL, mainText, e);
               if (Thread.currentThread() == mainThread) {
                  FabricGuiEntry.displayError(mainText, e, false);
               }
            }
         } catch (Throwable e2) { // just in case
            e.addSuppressed(e2);

            try {
               e.printStackTrace();
            } catch (Throwable e3) {
               PrintWriter pw = new PrintWriter(new FileOutputStream(FileDescriptor.err));
               e.printStackTrace(pw);
               pw.flush();
            }
         }
      });
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
