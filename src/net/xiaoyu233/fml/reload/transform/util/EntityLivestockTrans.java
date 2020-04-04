package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.EntityLivestock;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(EntityLivestock.class)
public class EntityLivestockTrans {
    @Marker
    protected void setWater(float water){}
    public void sWater(float water){
        this.setWater(water);
    }
    @Marker
    protected void setFood(float food){}
    public void sFood(float food){
        this.setFood(food);
    }
}
