package net.xiaoyu233.fml.reload.event;

import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.xiaoyu233.fml.reload.event.recipe.RecipeModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RecipeModifyEvent {
    private final Map<ItemInfo, RecipeModifier> modifiers = new HashMap<>();
    public void addModifier(RecipeModifier modifier){
        ItemStack output = modifier.getOutput();
        this.modifiers.put(new ItemInfo(output.getItem(), output.stackSize, output.getItemSubtype()), modifier);
    }

    public Map<ItemInfo, RecipeModifier> getModifiers() {
        return modifiers;
    }

    public static class ItemInfo{
        private final Item item;
        private final int count;
        private final int subtype;

        public ItemInfo(Item item, int count, int subtype) {
            this.item = item;
            this.count = count;
            this.subtype = subtype;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemInfo itemInfo = (ItemInfo) o;
            return count == itemInfo.count && subtype == itemInfo.subtype && Objects.equals(item, itemInfo.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, count, subtype);
        }
    }

}
