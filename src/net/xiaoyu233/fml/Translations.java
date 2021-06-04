package net.xiaoyu233.fml;

import java.util.HashMap;
import java.util.Map;

public class Translations {
   private static final Map<String, Map<String, String>> allTranslations = new HashMap();

   public static void addTranslationsFor(Map translations, String languageKey) {
      translations.putAll(allTranslations.getOrDefault(languageKey, new HashMap()));
   }

   public static void addLanguage(String languageKey) {
      allTranslations.put(languageKey, new HashMap());
   }

   public static Map<String, String> getLanguageMapFor(String langKey) {
      if (allTranslations.containsKey(langKey)) {
         return allTranslations.get(langKey);
      } else {
         HashMap<String, String> map = new HashMap();
         allTranslations.put(langKey, map);
         return map;
      }
   }

   static {
      Map<String, String> translation = getLanguageMapFor("en_US");
      translation.put("enchantment.slaying", "Slaying");
      translation.put("enchantment.cleaving", "Cleaving");
      translation.put("fishmodloader.update.available", "§7[FishModLoader]§6Fish Mod Loader§r's new version is available now,please download and update it on §9§n https://github.com/XiaoYuOvO/FishModLoader/releases §r,the latest version:§a%s");
      translation.put("fishmodloader.update.offline", "§7[FishModLoader]§cCannot check update for Fish Mod Loader");
      translation = getLanguageMapFor("zh_CN");
      translation.put("enchantment.slaying", "杀害");
      translation.put("enchantment.cleaving", "劈裂");
      translation.put("fishmodloader.update.available", "§7[FishModLoader]§6Fish Mod Loader§r有新版本可用,请到 §9§n https://github.com/XiaoYuOvO/FishModLoader/releases §r下载并更新,最新版本:§a%s");
      translation.put("fishmodloader.update.offline", "§7[FishModLoader]§c无法为FishModLoader检查更新");
   }
}
