package net.xiaoyu233.fml.reload.transform.entrypoint;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.FishModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ServerEntrypointMixin {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/StatList;nopInit()V", shift = At.Shift.BEFORE), require = 1)
    private static void injectMain(CallbackInfo callbackInfo){
        FishModLoader.invokeEntrypoints("main", ModInitializer.class, modInitializer -> {
            modInitializer.createConfig().ifPresent(configRegistry -> {
                FishModLoader.addConfigRegistry(configRegistry);
                configRegistry.reloadConfig();
            });
            modInitializer.onInitialize();
        });
        FishModLoader.invokeEntrypoints("server", DedicatedServerModInitializer.class, DedicatedServerModInitializer::onInitializeServer);
    }
}
