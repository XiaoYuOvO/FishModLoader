package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.DebugAttack;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.config.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(DebugAttack.class)
public class DebugAttackTrans {
    @Redirect(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;inDevMode()Z"))
    private static boolean redirectShouldPrintDamageInfo() {
        return Configs.Debug.PRINT_ENTITY_DAMAGE_INFO.get() || Minecraft.inDevMode();
    }

    @Redirect(method = "flushInstance", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/Object;)V"))
    private void flushInstance(PrintStream stream, Object info) {
        MinecraftServer.getServer().getLogAgent().logInfo(info.toString());
    }
}
