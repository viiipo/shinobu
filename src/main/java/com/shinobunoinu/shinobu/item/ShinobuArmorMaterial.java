package com.shinobunoinu.shinobu.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ArmorItem.Type;

public class ShinobuArmorMaterial implements ArmorMaterial {
    private static final int[] DURABILITY = {13, 15, 16, 11}; // HELMET, CHEST, LEGS, BOOTS
    private static final int[] DEFENSE = {6, 0, 0, 0};        // 仅头盔提供护甲

    @Override
    public int getDurabilityForType(Type type) {
        return DURABILITY[type.ordinal()] * 25;
    }

    @Override
    public int getDefenseForType(Type type) {
        return DEFENSE[type.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public net.minecraft.sounds.SoundEvent getEquipSound() {
        return net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.LEATHER);
    }

    @Override
    public String getName() {
        return "shinobu_hat";
    }

    @Override
    public float getToughness() {
        return 0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0f;
    }
}
