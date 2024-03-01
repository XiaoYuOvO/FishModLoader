package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Skill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Skill.class)
public interface SkillAccessor {
    @Invoker("getById")
    static Skill getById(int id) {
        throw new AssertionError();
    }

    @Invoker("getSkillsByIds")
    static Skill[] getSkillsByIds(int ids){
        throw new AssertionError();
    }

    @Invoker("getByLocalizedName")
    static Skill getByLocalizedName(String localized_name, boolean profession_name){
        throw new AssertionError();
    }

    @Invoker("getSkillsString")
    static String getSkillsString(int ids, boolean profession_names, String delimiter){
        throw new AssertionError();
    }

    @Accessor("num_skills")
    static int getNumSkills(){
        throw new AssertionError();
    }

    @Accessor("num_skills")
    static void setNumSkills(int skills){
        throw new AssertionError();
    }

    @Accessor
    int getId();

    @Accessor("unlocalized_name")
    String getUnlocalizedName();
}
