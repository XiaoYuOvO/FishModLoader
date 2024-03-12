package net.xiaoyu233.fml.reload.event;

import net.minecraft.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantmentRegistryEvent {
    private final List<Enchantment> enchantmentList = new ArrayList<>();

    public void registerEnchantment(Enchantment enchantment){
        enchantmentList.add(enchantment);
    }
    public void registerEnchantment(Enchantment... enchantment){
        enchantmentList.addAll(Arrays.asList(enchantment));
    }

    public List<Enchantment> getEnchantmentList() {
        return enchantmentList;
    }
}
