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
      translation.put("fishmodloader.update.available", "\u00a77[FishModLoader]\u00a76Fish Mod Loader\u00a7r's new version is available now,please download and update it on \u00a79\u00a7n https://github.com/XiaoYuOvO/FishModLoader/releases \u00a7r,the latest version:\u00a7a%s");
      translation.put("fishmodloader.update.offline", "\u00a77[FishModLoader]\u00a7cCannot check update for Fish Mod Loader");
      translation = getLanguageMapFor("zh_CN");
      translation.put("enchantment.slaying", "\u6740\u5bb3");
      translation.put("enchantment.cleaving", "\u5288\u88c2");
      translation.put("fishmodloader.update.available", "\u00a77[FishModLoader]\u00a76Fish Mod Loader\u00a7r\u6709\u65b0\u7248\u672c\u53ef\u7528,\u8bf7\u5230 \u00a79\u00a7n https://github.com/XiaoYuOvO/FishModLoader/releases \u00a7r\u4e0b\u8f7d\u5e76\u66f4\u65b0,\u6700\u65b0\u7248\u672c:\u00a7a%s");
      translation.put("fishmodloader.update.offline", "\u00a77[FishModLoader]\u00a7c\u65e0\u6cd5\u4e3aFishModLoader\u68c0\u67e5\u66f4\u65b0");
   }
}
