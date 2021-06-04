package net.xiaoyu233.fml.util;

import com.google.common.collect.Lists;
import net.xiaoyu233.fml.AbstractMod;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.ArrayList;

public class RemoteModInfo {
    private final ArrayList<MixinEnvironment.Side> dists;
    private final int modVerNum;
    private final String modVerStr;
    private final String modid;

    public RemoteModInfo(AbstractMod mod, MixinEnvironment.Side... dist) {
        this.modid = mod.modId();
        this.modVerStr = mod.modVerStr();
        this.modVerNum = mod.modVerNum();
        this.dists = Lists.newArrayList(dist);
    }

    public boolean canBeUsedAt(MixinEnvironment.Side dist) {
        return this.dists.contains(dist);
    }

    public int getModVerNum() {
        return this.modVerNum;
    }

    public String getModVerStr() {
        return this.modVerStr;
    }

    public String getModid() {
        return this.modid;
    }
}
