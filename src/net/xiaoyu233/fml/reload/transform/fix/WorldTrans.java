package net.xiaoyu233.fml.reload.transform.fix;

import net.minecraft.World;
import net.minecraft.WorldProvider;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(World.class)
public class WorldTrans {
    @Link
    public WorldProvider t;
    public WorldProvider getT() {
        return t;
    }
}
