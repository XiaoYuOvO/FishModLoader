package net.xiaoyu233.fml;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import net.xiaoyu233.fml.config.ConfigRegistry;
import net.xiaoyu233.fml.config.Configs;
import net.xiaoyu233.fml.config.InjectionConfig;
import net.xiaoyu233.fml.reload.transform.MinecraftServerTrans;
import net.xiaoyu233.fml.util.ModInfo;
import net.xiaoyu233.fml.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.service.MixinService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class FishModLoader extends AbstractMod{
   public static final File CONFIG_DIR = new File("configs");
   public static final Logger LOGGER = LogManager.getLogger("FishModLoader");
   public static final File MOD_DIR = new File("mods");
   private static final Map<String, ModInfo> modsMapForLoginCheck;
   private static final boolean allowsClientMods;
   public static final String VERSION = "v1.4.2";
   public static final int VERSION_NUM = 142;
   private static final ArrayList<ModInfo> mods = new ArrayList<>();
   private static final Map<String, ModInfo> modsMap = new HashMap<>();
   private static boolean isServer = false;
   private static final String onlineVersion = versionCheck();
   private static final List<ConfigRegistry> ALL_REGISTRIES = new ArrayList<>();
   private static final ConfigRegistry CONFIG_REGISTRY = new ConfigRegistry(Configs.CONFIG,Configs.CONFIG_FILE);

   static {
      try {
         UIManager.setLookAndFeel(new WindowsLookAndFeel());
      } catch (Exception ignored) {
      }

      if (isServer()) {
         allowsClientMods = Configs.Server.allowClientMods.get();
      } else {
         allowsClientMods = true;
      }

      modsMapForLoginCheck = new HashMap<>();
      addModInfo(new ModInfo(new FishModLoader(), Lists.newArrayList(MixinEnvironment.Side.SERVER, MixinEnvironment.Side.CLIENT)));
   }

   private FishModLoader(){

   }

   public static void addModInfo(ModInfo modInfo) {
      if (!modsMap.containsKey(modInfo.getModid())){
         mods.add(modInfo);
         modsMap.put(modInfo.getModid(), modInfo);
         if (modInfo.canBeUsedAt(MixinEnvironment.Side.CLIENT)) {
            modsMapForLoginCheck.put(modInfo.getModid(), modInfo);
         }
      }
   }

   public static void addConfigRegistry(ConfigRegistry configRegistry){
      if (!ALL_REGISTRIES.contains(configRegistry)){
         ALL_REGISTRIES.add(configRegistry);
      }
   }

   public static List<ConfigRegistry> getAllConfigRegistries() {
       return ALL_REGISTRIES;
   }

   public static void reloadAllConfigs(){
      mods.stream().map(ModInfo::getMod).map(AbstractMod::getConfigRegistry).filter(Objects::nonNull).forEach(FishModLoader::addConfigRegistry);
      for (int i = 0; i < ALL_REGISTRIES.size(); i++) {
         ConfigRegistry configRegistry = ALL_REGISTRIES.get(i);
         configRegistry.reloadConfig();
      }
   }

   @Nullable
   @Override
   public ConfigRegistry getConfigRegistry() {
      return CONFIG_REGISTRY;
   }

   public static ImmutableMap<String, ModInfo> getModsMap() {
      return new ImmutableMap.Builder<String, ModInfo>().putAll(modsMap).build();
   }

   public static void extractOpenAL(){
      File file = new File(System.getProperty("java.library.path"));
      try {
         Utils.extractFileFromJar("/OpenAL64.dll",new File(file,"OpenAL64.dll"),true);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static int getFpsLimit() {
      return Configs.Client.fpsLimit.get();
   }

   public static JsonElement getModsJson() {
      return (new Gson()).toJsonTree(mods);
   }

   public static boolean hasMod(String modid){
      return modsMap.containsKey(modid);
   }

   public static String getOnlineVersion() {
      return onlineVersion;
   }

   public static boolean isServer() {
      return isServer;
   }

   public static Map<String, ModInfo> getModsMapForLoginCheck() {
      return new HashMap<>(modsMapForLoginCheck);
   }

   public static MixinEnvironment.Side getSide(){
      return isServer ? MixinEnvironment.Side.SERVER : MixinEnvironment.Side.CLIENT;
   }

   public static void loadConfig() {
      Configs.loadConfig();
   }

   public static void registerModloaderMixin(ClassLoader classLoader){
      Mixins.registerConfiguration((InjectionConfig.Builder.of("FishModLoader", MinecraftServerTrans.class.getPackage(), MixinEnvironment.Phase.INIT).build().toConfig(classLoader, MixinService.getService(),MixinEnvironment.getCurrentEnvironment())));
   }

   public static void setIsServer(boolean isServer) {
      FishModLoader.isServer = isServer;
   }

   public static boolean isAllowsClientMods() {
      return allowsClientMods;
   }

   public static String versionCheck() {
      try {
         URL url = new URL("https://raw.githubusercontent.com/XiaoYuOvO/FishModLoader/master/VERSION.txt");
         HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
         SSLContext sc = SSLContext.getInstance("TLSv1.2");
         sc.init(null, null, new SecureRandom());
         connection.setSSLSocketFactory(sc.getSocketFactory());
         connection.setRequestMethod("GET");
         connection.setDoInput(true);
         connection.setInstanceFollowRedirects(false);
         connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
         connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
         connection.setRequestProperty("Accept-Encoding", "text");
         connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
         connection.setRequestProperty("Cache-Control", "max-age=0");
         connection.setRequestProperty("Host", "raw.githubusercontent.com");
         connection.setRequestProperty("Connection", "keep-alive");
         connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
         connection.connect();
         return (new Scanner(connection.getInputStream(), String.valueOf(StandardCharsets.UTF_8))).nextLine();
      } catch (IOException ignored) {
      } catch (KeyManagementException | NoSuchAlgorithmException var4) {
         var4.printStackTrace();
      }

      return null;
   }

   @Nonnull
   @Override
   public InjectionConfig getInjectionConfig() {
      return InjectionConfig.Builder.of("FishModLoader", MinecraftServerTrans.class.getPackage(), MixinEnvironment.Phase.INIT).build();
   }

   @Override
   public String modId() {
      return "FishModLoader";
   }

   @Override
   public int modVerNum() {
      return VERSION_NUM;
   }

   @Override
   public String modVerStr() {
      return VERSION;
   }

   @Override
   public void preInit() {

   }
}
