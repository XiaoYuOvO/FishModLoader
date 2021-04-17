package net.xiaoyu233.fml.util;

import com.google.common.collect.Lists;
import net.xiaoyu233.fml.AbstractMod;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.ArrayList;

public class ModInfo {
   private final String modid;
   private final String modVerStr;
   private final int modVerNum;
   private final ArrayList<MixinEnvironment.Side> dists;
   private final AbstractMod mod;

   public ModInfo(AbstractMod mod, MixinEnvironment.Side... dist) {
      this.modid = mod.modId();
      this.modVerStr = mod.modVerStr();
      this.modVerNum = mod.modVerNum();
      this.dists = Lists.newArrayList(dist);
      this.mod = mod;
   }

   public AbstractMod getMod() {
      return mod;
   }

   public int getModVerNum() {
      return this.modVerNum;
   }

   public String getModid() {
      return this.modid;
   }

   public String getModVerStr() {
      return this.modVerStr;
   }

   public boolean canBeUsedAt(MixinEnvironment.Side dist) {
      return this.dists.contains(dist);
   }
}
