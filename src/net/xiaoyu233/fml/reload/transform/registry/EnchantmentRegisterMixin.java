package net.xiaoyu233.fml.reload.transform.registry;

import net.minecraft.Enchantment;
import net.xiaoyu233.fml.reload.event.EnchantmentRegistryEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.Enchantment.enchantmentsList;

@Mixin(Enchantment.class)
public class EnchantmentRegisterMixin {
    @Shadow @Final @Mutable
    public static Enchantment[] enchantmentsBookList;

    @Inject(method = "<clinit>" , at = @At("RETURN"))
    private static void injectRegister(CallbackInfo callbackInfo){
        EnchantmentRegistryEvent event = new EnchantmentRegistryEvent();
        MITEEvents.MITE_EVENT_BUS.post(event);
        registerEnchantmentsUnsafe(event.getEnchantmentList());
    }

    private static void registerEnchantmentsUnsafe(List<Enchantment> enchantments) {
        for (int i = 0, bLength = enchantmentsList.length; i < bLength; i++) {
            if (enchantmentsList[i] == null) {
                for (int j = 0, enchantmentsLength = enchantments.size(); j < enchantmentsLength; j++) {
                    enchantmentsList[i + j] = enchantments.get(j);
                }
                break;
            }
        }
        ArrayList<Enchantment> var0 = new ArrayList<>();
        for (Enchantment enchantment : enchantmentsList) {
            if (enchantment != null) {
                var0.add(enchantment);
            }
        }
        enchantmentsBookList = var0.toArray((new Enchantment[0]));
    }
}
