package net.xiaoyu233.fml.classloading;

import org.spongepowered.asm.mixin.MixinEnvironment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    MixinEnvironment.Side[] value() default {MixinEnvironment.Side.CLIENT, MixinEnvironment.Side.SERVER};
}
