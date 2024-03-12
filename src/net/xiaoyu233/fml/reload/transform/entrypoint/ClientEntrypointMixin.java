package net.xiaoyu233.fml.reload.transform.entrypoint;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.main.Main;
import net.xiaoyu233.fml.FishModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class ClientEntrypointMixin {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;run()V", shift = At.Shift.BEFORE))
    private static void injectMain(CallbackInfo callbackInfo){
        FishModLoader.invokeEntrypoints("main", ModInitializer.class, ModInitializer::onInitialize);
        FishModLoader.invokeEntrypoints("client", ClientModInitializer.class, ClientModInitializer::onInitializeClient);
    }
}
