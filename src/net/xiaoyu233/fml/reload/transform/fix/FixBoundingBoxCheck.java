package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.EntityHuman;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(EntityHuman.class)
public class FixBoundingBoxCheck {
    public void checkBoundingBoxAgainstSolidBlocks() {
    }
}
