package net.xiaoyu233.fml.util;

public class IdUtil {
    private static int nextItemID = 4000;
    private static int nextBlockID = 4095;
    private static int nextEnchantmentID = 96;
    private static int nextAchievementID = 136;
    private static int nextEntityID = 200;
    private static int nextPacketID = 134;

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
}
