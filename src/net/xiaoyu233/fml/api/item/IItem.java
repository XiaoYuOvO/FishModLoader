package net.xiaoyu233.fml.api.item;

import net.minecraft.Item;
import net.xiaoyu233.fml.api.INamespaced;

public interface IItem extends INamespaced {
    default Item setItemTextureName(String location){throw new AssertionError();};
    default String getTexturePrefix(){throw new AssertionError();};
}
