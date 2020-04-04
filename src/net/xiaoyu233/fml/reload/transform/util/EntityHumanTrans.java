package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.EntityHuman;
import net.minecraft.EntityPlayer;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(EntityHuman.class)
public class EntityHumanTrans {
    @Marker
    protected static final int getExperienceRequired(int level){
        return 0;
    }

    public static int getExpRequired(int level){
        return getExperienceRequired(level);
    }
}
