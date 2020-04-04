package net.xiaoyu233.fml.reload.transform.util;

import net.minecraft.Skill;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;

@Transform(Skill.class)
public class SkillTrans {
    @Link
    private int id;

    @Marker
    static Skill getByLocalizedName(String localized_name, boolean profession_name){
        return null;
    }

    @Marker
    static String getSkillsString(int ids, boolean profession_names, String delimiter){
        return "";
    }

    public static String getSkillsStr(int ids, boolean profession_names, String delimiter){
        return getSkillsString(ids,profession_names,delimiter);
    }

    public int getID(){
        return this.id;
    }

    public static Skill getByLcName(String localized_name, boolean profession_name){
        return getByLocalizedName(localized_name,profession_name);
    }
}
