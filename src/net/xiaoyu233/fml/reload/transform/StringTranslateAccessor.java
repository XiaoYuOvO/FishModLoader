package net.xiaoyu233.fml.reload.transform;

import net.minecraft.StringTranslate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StringTranslate.class)
public interface StringTranslateAccessor {
    @Accessor
    Map getLanguageList();
}
