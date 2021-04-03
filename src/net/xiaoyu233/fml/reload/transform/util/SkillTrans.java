package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.BitHelper;
import net.minecraft.Skill;
import net.minecraft.StringHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import static net.minecraft.Skill.getNumSkills;
import static net.xiaoyu233.fml.util.ReflectHelper.dyCast;

@Mixin(Skill.class)
public abstract class SkillTrans {
    @Shadow
    private static int num_skills;
    @Shadow
    @Final
    private int id;

   @Invoker("getByLocalizedName")
   public static Skill getByLcName(String localized_name, boolean profession_name) {
      return getByLocalizedName(localized_name, profession_name);
   }

   @Shadow
   static Skill getByLocalizedName(String localized_name, boolean profession_name) {
      return null;
   }

    @Invoker("getSkillsByIds")
   public static Skill[] getSkillsByIds(int ids) {
       int num_skills_present = getNumSkills(ids);
       if (num_skills_present == 0) {
           return null;
       } else {
           Skill[] skills = new Skill[num_skills_present];
           int j = 0;

           for(int i = 0; i < num_skills; ++i) {
               if (BitHelper.isBitSet(ids, dyCast(SkillTrans.class,Skill.list[i]).id)) {
                   skills[j++] = Skill.list[i];
               }
           }

           return j == 0 ? null : skills;
       }
   }

    @Invoker("getSkillsString")
    public static String getSkillsString(int ids, boolean profession_names, String delimiter) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < num_skills; ++i) {
            Skill skill = Skill.list[i];
            if (BitHelper.isBitSet(ids, dyCast(SkillTrans.class,skill).id)) {
                sb.append(skill.getLocalizedName(profession_names)).append(delimiter);
            }
        }

        String s = sb.toString();
        return s.isEmpty() ? null : StringHelper.left(s, -delimiter.length());
    }

   @Accessor("id")
   public abstract int getID();
}
