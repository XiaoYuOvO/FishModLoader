package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.ChatAllowedCharacters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.InputStream;
import java.util.Objects;

@Mixin(ChatAllowedCharacters.class)
public class AllowedCharFix {
    @Redirect(method = "getAllowedCharacters", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"))
    private static InputStream redirectAddAllowedCharInputStream(Class<?> clazz, String name){
        return Objects.requireNonNull(AllowedCharFix.class.getResourceAsStream("/font.txt"));
    }
}
