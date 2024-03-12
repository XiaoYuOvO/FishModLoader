package net.xiaoyu233.fml.util;

import com.chocohead.mm.EnumExtender;
import com.chocohead.mm.api.ClassTinkerers;
import net.minecraft.EnumEquipmentMaterial;

public class EnumExtends {
    public static final EnumExtender CHAT_FORMATTING = ClassTinkerers.enumBuilder();
    public static final EnumConstructor<EnumEquipmentMaterial> EQUIPMENT_MATERIAL = new EnumConstructor<>(EnumEquipmentMaterial.class,0);
//    public static final EnumConstructor<EnumToolMaterial> TOOL_MATERIAL = new EnumConstructor<EnumToolMaterial>(EnumToolMaterial.class);
//    public static final EnumConstructor<EnumArmorMaterial> ARMOR_MATERIAL = new EnumConstructor<EnumArmorMaterial>(EnumArmorMaterial.class);
}
