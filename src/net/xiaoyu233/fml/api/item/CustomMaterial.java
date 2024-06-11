package net.xiaoyu233.fml.api.item;

import net.minecraft.EnumEquipmentMaterial;
import net.minecraft.MapColor;
import net.minecraft.Material;

public class CustomMaterial extends Material {
    private final float toolEffective;
    public CustomMaterial(String name, float toolEffective) {
        super(name);
        this.toolEffective = toolEffective;
    }

    public CustomMaterial(String name, MapColor map_color, float toolEffective) {
        super(name, map_color);
        this.toolEffective = toolEffective;
    }

    public CustomMaterial(EnumEquipmentMaterial enum_crafting_material, float toolEffective) {
        super(enum_crafting_material);
        this.toolEffective = toolEffective;
    }

    public float getToolEffective() {
        return toolEffective;
    }
}
