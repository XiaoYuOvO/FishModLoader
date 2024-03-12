package net.xiaoyu233.fml.reload.transform.enum_extend;

import net.minecraft.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EnumChatFormatting.class, priority = 10000)
public class EnumChatFormattingMixin {
//    @Redirect(method = "getByChar", at = @At(value = "INVOKE", target = "Lnet/minecraft/EnumChatFormatting;values()[Lnet/minecraft/EnumChatFormatting;"))
//    private static EnumChatFormatting[] redirectExtendedChatFormatting(){
//        return EnumExtends.CHAT_FORMATTING.values();
//    }
}
