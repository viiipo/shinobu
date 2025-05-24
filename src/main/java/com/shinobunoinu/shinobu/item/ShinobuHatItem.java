package com.shinobunoinu.shinobu.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ShinobuHatItem extends ArmorItem {

    public ShinobuHatItem(ArmorMaterial material, Item.Properties properties) {
        // ✔ 正确用法：使用 ArmorItem.Type.HELMET 取代 EquipmentSlot
        super(material, Type.HELMET, properties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.ARMOR_HEAD || super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
