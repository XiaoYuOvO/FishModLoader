package net.xiaoyu233.fml.relaunch;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.accesswidener.AccessWidener;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FormattedException;
import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.TinyUtils;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.classloading.KnotClassLoaderInterface;
import net.xiaoyu233.fml.mapping.CachedMappedJar;
import net.xiaoyu233.fml.util.LogProxy;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Launch {
   public static Map<String, Object> blackboard = new HashMap<>();
   private static final Map<String, String> unmapClassMap = new HashMap<>();
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
//      Set<InterfaceInjection> injections = Sets.newHashSet(BuiltinInjection.getModLoaderInjection());
      try {
         IMappingProvider tinyMappingProvider = TinyUtils.createTinyMappingProvider(new BufferedReader(new InputStreamReader(Objects.requireNonNull(Launch.class.getResourceAsStream("/mappings.tiny")))), "official", "named");
         tinyMappingProvider.load(createAcceptor());
         tinyMappingProvider = TinyUtils.createTinyMappingProvider(new BufferedReader(new InputStreamReader(Objects.requireNonNull(Launch.class.getResourceAsStream("/mappings.tiny")))), "official", "named");
         CachedMappedJar cachedMappedJar = new CachedMappedJar(gameJarPath, tinyMappingProvider, new File(minecraftHome)
//                 ,injections
         );
         knotInterface.addCodeSource(cachedMappedJar.ensureJarMapped());
      }catch (Exception e){
         throw new RuntimeException("Cannot remap minecraft jar", e);
      }

      ClassLoader knotLoader = knotInterface.getClassLoader();
      Thread.currentThread().setContextClassLoader(knotLoader);
      FishModLoader.setup();
      onEnvironmentChanged();

//      ModsWalker modsWalker = new ModsWalker(FishModLoader.MOD_DIR, knotLoader);
//      for (InjectionConfig loadMod : modsWalker.loadMods(modsWalker.findMods(jarFile -> addModToLoader(knotInterface, jarFile)), (abstractMod, dists) -> {
//         abstractMod.getInterfaceInjections().ifPresent(injections::add);
//         modRegister(abstractMod, dists);
//      })) {
//         Mixins.registerConfiguration(loadMod.toConfig(knotLoader, MixinService.getService(),MixinEnvironment.getCurrentEnvironment()));
//      }
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
            FishModLoader.invokeEntrypoints("preLaunch", PreLaunchEntrypoint.class, preLaunchEntrypoint -> {
               preLaunchEntrypoint.onPreLaunch();
               preLaunchEntrypoint.createConfig().ifPresent(FishModLoader::addConfigRegistry);
            });
         } catch (RuntimeException e) {
            throw FormattedException.ofLocalized("exception.initializerFailure", e);
         }
         FishModLoader.reloadAllConfigs();
//         knotInterface.unlockBlocking();
         knotLoader.loadClass(mainClass).getMethod("main", String[].class).invoke(null, (Object) args);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
         FishModLoader.LOGGER.error("Cannot launch minecraft", e);
      }

   }




   private static IMappingProvider.MappingAcceptor createAcceptor(){
      return new IMappingProvider.MappingAcceptor() {
         @Override
         public void acceptClass(String srcName, String dstName) {
            unmapClassMap.put(dstName, srcName);
         }

         @Override
         public void acceptMethod(IMappingProvider.Member method, String dstName) {

         }

         @Override
         public void acceptMethodArg(IMappingProvider.Member method, int lvIndex, String dstName) {

         }

         @Override
         public void acceptMethodVar(IMappingProvider.Member method, int lvIndex, int startOpIdx, int asmIndex, String dstName) {

         }

         @Override
         public void acceptField(IMappingProvider.Member field, String dstName) {

         }
      };
   }

   private static void addModToLoader(KnotClassLoaderInterface nativeClassLoader, File jarFile) {
      try {
         URL url = jarFile.toURI().toURL();
         nativeClassLoader.addCodeSource(jarFile.toPath());
         FishModLoader.LOGGER.info("Found mod jar:" + url);
      } catch (MalformedURLException e) {
         FishModLoader.LOGGER.error("Failed to add mod file to loader", e);
      }
   }

   public static String unmapClassName(String mappedName){
      return unmapClassMap.get(mappedName);
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

   public static AccessWidener getAccessWidener() {
      return null;
   }

//   private static void modRegister(AbstractMod abstractMod, MixinEnvironment.Side[] dists) {
//      if (!FishModLoader.hasMod(abstractMod.modId())) {
//         abstractMod.preInit();
//         FishModLoader.addModInfo(new ModInfo(abstractMod, Lists.newArrayList(dists)));
//      } else {
//         if (!FishModLoader.isServer()) {
//            UIUtils.showErrorDialog("错误,模组重复!游戏即将退出\n" + abstractMod.modId() + "-" + abstractMod.modVerStr());
//            System.exit(1);
//         }
//         throw new IllegalArgumentException("Duplicated mods! (重复的模组)" + abstractMod.modId() + "-" + abstractMod.modVerStr());
//      }
//   }
}
