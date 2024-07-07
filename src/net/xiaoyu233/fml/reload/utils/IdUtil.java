package net.xiaoyu233.fml.reload.utils;

import net.minecraft.BiomeGenBase;
import net.minecraft.Curse;
import net.minecraft.Potion;
import net.minecraft.Skill;
import net.xiaoyu233.fml.util.RangedIncrementNumber;

public class IdUtil {
    private static int nextItemID = 4000;
    private static int nextBlockID = 4095;
    private static int nextEnchantmentID = 96;
    private static int nextAchievementID = 136;
    private static int nextEntityID = 200;
    private static int nextPacketID = 134;
    //24-32
    private static final RangedIncrementNumber NEXT_POTION_ID = RangedIncrementNumber.create(24, Potion.potionTypes.length - 1);
    //17-63
    private static final RangedIncrementNumber NEXT_CURSE_ID = RangedIncrementNumber.create(17, Curse.cursesList.length - 1);
    private static final RangedIncrementNumber NEXT_BIOME_ID = RangedIncrementNumber.create(27, BiomeGenBase.biomeList.length - 1);
    private static final RangedIncrementNumber NEXT_SKILL_ID = RangedIncrementNumber.create(26, Skill.list.length - 1);
    private static int nextRenderType = 255;

    public static int getNextEnchantmentID(){
        return nextEnchantmentID++;
    }

    public static int getNextItemID() {
        return nextItemID++;
    }

    public static int getNextBlockID() {
        return nextBlockID--;
    }

    public static int getNextAchievementID(){
        return nextAchievementID++;
    }

    public static int getNextEntityID(){
        return nextEntityID++;
    }

    public static int getNextPacketID(){
        return nextPacketID++;
    }

    public static int getNextRenderType(){
        return nextRenderType++;
    }

    public static int getNextPotionId(){
        return NEXT_POTION_ID.getNext();
    }

    public static int getNextCurseId(){
        return NEXT_CURSE_ID.getNext();
    }
    public static int getNextBiomeId(){
        return NEXT_BIOME_ID.getNext();
    }
    public static int getNextSkillId(){
        return NEXT_SKILL_ID.getNext();
    }
}
