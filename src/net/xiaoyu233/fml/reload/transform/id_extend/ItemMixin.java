package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin {
    //Dont do it! It will mess up the id table
//    @ModifyConstant(method = "<init>(ILjava/lang/String;I)V", constant = @Constant(intValue = 256))
//    private static int injected(int value) {
//        return 1024;
//    }
}
