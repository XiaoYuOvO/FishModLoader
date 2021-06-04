package net.xiaoyu233.fml.util;

import net.xiaoyu233.fml.AbstractMod;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.List;
import java.util.stream.Collectors;

public class ModInfo {
   private final String modid;
   private final String modVerStr;
   private final int modVerNum;
   private final List<String> dists;
   private final AbstractMod mod;

   public ModInfo(AbstractMod mod, List<MixinEnvironment.Side> dist) {
      this.modid = mod.modId();
      this.modVerStr = mod.modVerStr();
      this.modVerNum = mod.modVerNum();
      this.dists = ((List<?>)dist).stream().map((Object::toString)).collect(Collectors.toList());
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
      return this.dists.stream().anyMatch((dists) -> dists.equals(dist.name()));
   }

   public List<MixinEnvironment.Side> getDists() {
      return this.dists.stream().map(MixinEnvironment.Side::valueOf).collect(Collectors.toList());
   }
}
