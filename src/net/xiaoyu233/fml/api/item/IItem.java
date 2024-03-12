package net.xiaoyu233.fml.api.item;

import net.minecraft.Item;

public interface IItem {
    default Item setItemTextureName(String location){throw new AssertionError();};
    default String getTexturePrefix(){throw new AssertionError();};
}
