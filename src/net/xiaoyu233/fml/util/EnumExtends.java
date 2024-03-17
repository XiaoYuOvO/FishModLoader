package net.xiaoyu233.fml.util;

import com.chocohead.mm.api.ClassTinkerers;
import com.chocohead.mm.api.EnumAdder;

public class EnumExtends {
    public static final EnumAdder CHAT_FORMATTING = ClassTinkerers.enumBuilder("net.minecraft.EnumChatFormatting", char.class, int.class, int.class, int.class);
    public static final EnumAdder EQUIPMENT_MATERIAL = ClassTinkerers.enumBuilder("net.minecraft.EnumEquipmentMaterial",float.class, int.class, "Lnet.minecraft.EnumQuality;", String.class);
//    public static final EnumConstructor<EnumToolMaterial> TOOL_MATERIAL = new EnumConstructor<EnumToolMaterial>(EnumToolMaterial.class);
//    public static final EnumConstructor<EnumArmorMaterial> ARMOR_MATERIAL = new EnumConstructor<EnumArmorMaterial>(EnumArmorMaterial.class);

    public static void buildEnumExtending(){
        CHAT_FORMATTING.build();
        EQUIPMENT_MATERIAL.build();
    }
}
