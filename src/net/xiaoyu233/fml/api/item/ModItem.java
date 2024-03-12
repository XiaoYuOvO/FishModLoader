package net.xiaoyu233.fml.api.item;

import net.minecraft.Material;

public class ModItem extends net.minecraft.Item {
    public ModItem(){
        super();
    }

    public ModItem(int id, String texture) {
        super(id, texture, -1);
    }

    public ModItem(int par1, String texture, int num_subtypes){
        super(par1, texture, num_subtypes);
    }

    public ModItem(int id, Material material, String texture){
        super(id, material, texture);
    }

    public ModItem(int id, Material[] material_array, String texture){
        super(id, material_array, texture);
    }
}
