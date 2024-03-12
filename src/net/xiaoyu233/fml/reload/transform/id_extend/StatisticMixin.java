package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.StatList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StatList.class)
public class StatisticMixin {
    @ModifyConstant(method = {
            "initBreakableStats",
            "initStats",
            "initMinableStats",
            "initUsableStats"
    }, constant = @Constant(intValue = 256))
    private static int injected(int value) {
        return 1024;
    }
}
