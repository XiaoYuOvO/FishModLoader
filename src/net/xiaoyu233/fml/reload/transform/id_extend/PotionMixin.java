package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Potion.class)
public class PotionMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 32, ordinal = 0), require = 1, allow = 1)
    private static int modifyPotionIdMax(int originalId){
        //Limited to 256 by Packet41EntityEffect::<init>-> potionId & 0xff
        return 256;
    }
}
