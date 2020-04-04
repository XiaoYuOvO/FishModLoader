package net.xiaoyu233.fml.reload.transform.util;

import net.xiaoyu233.fml.asm.annotations.Link;

import java.util.Set;

public class WorldServerTrans {
    @Link
    protected Set G;
    public Set getG() {
        return G;
    }
}
