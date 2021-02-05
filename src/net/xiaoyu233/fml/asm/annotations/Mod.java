package net.xiaoyu233.fml.asm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Mod {
   Dist[] value() default {Dist.CLIENT, Dist.SERVER};
}
