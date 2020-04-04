package net.xiaoyu233.fml.reload.transform;

import com.google.common.base.Splitter;
import net.minecraft.LocaleLanguage;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Transform;

import java.util.Map;
import java.util.regex.Pattern;

@Transform(LocaleLanguage.class)
public class LocaleLanguageTrans {
    @Link
    private static final Pattern a = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    @Link
    private static final Splitter b = Splitter.on('=').limit(2);
    @Link
    private static LocaleLanguage c = new LocaleLanguage();
    @Link
    private Map d;
    public static void addTranslation(String key,String value){
        c.getTranslationMap().put(key,value);
    }

    public Map getTranslationMap(){
        return this.d;
    }

    static {
        addTranslation("enchantment.slaying","杀害");
    }
}
