package com.shinobunoinu.shinobu.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DecorationItem extends Item {
    public DecorationItem(Properties properties) {
        super(properties);
    }

    // 标识这是帽子物品
    public static boolean isHat(ItemStack stack) {
        return stack.getItem() instanceof DecorationItem;
    }
}