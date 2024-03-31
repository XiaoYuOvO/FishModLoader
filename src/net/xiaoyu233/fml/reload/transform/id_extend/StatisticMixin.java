package net.xiaoyu233.fml.reload.transform.id_extend;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.Item;
import net.minecraft.ItemBlock;
import net.minecraft.StatList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StatList.class)
public class StatisticMixin {
    @ModifyConstant(method = {
            "initBreakableStats",
            "initStats",
    }, constant = @Constant(intValue = 256))
    private static int injected(int value) {
        return 4096;
    }

    @ModifyConstant(method = "initMinableStats", constant = @Constant(intValue = 256))
    private static int modifyBlockStatsIndexLimit(int val){
        return 4096;
    }

    @ModifyConstant(method = "initUsableStats", constant = @Constant(intValue = 256))
    private static int modifyUsableItemIndex(int value, @Local(index = 5) int var5){
        if (Item.itemsList[var5] instanceof ItemBlock) return 4096;
        return value;
    }


}
