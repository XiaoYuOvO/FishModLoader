package net.xiaoyu233.fml;

import java.util.HashMap;
import java.util.Map;

public class Translations {
    private static Map<String,Map<String,String>> allTranslations = new HashMap<>();
    public static void addTranslationsFor(Map translations,String languageKey){
        translations.putAll(allTranslations.getOrDefault(languageKey,new HashMap<>()));
    }

    public static void addLanguage(String languageKey){
        allTranslations.put(languageKey,new HashMap<>());
    }

    public static Map<String,String> getLanguageMapFor(String langKey){
        if (allTranslations.containsKey(langKey)){
            return allTranslations.get(langKey);
        }else{
            HashMap<String,String> map = new HashMap<>();
            allTranslations.put(langKey,map);
            return map;
        }
    }

    static {
        Map<String,String> translation = getLanguageMapFor("en_US");
        translation.put("enchantment.slaying","Slaying");
        translation.put("enchantment.cleaving","Cleaving");
        translation = getLanguageMapFor("zh_CN");
        translation.put("enchantment.slaying","杀害");
        translation.put("enchantment.cleaving","劈裂");
    }
}
