package net.xiaoyu233.fml.reload.event;

import java.util.Map;

public class LanguageResourceReloadEvent {
   private final Map translation;
   private final String languageKey;

   public LanguageResourceReloadEvent(Map translation, String languageKey) {
      this.translation = translation;
      this.languageKey = languageKey;
   }

   public Map getTranslation() {
      return this.translation;
   }

   public String getLanguageKey() {
      return this.languageKey;
   }
}
