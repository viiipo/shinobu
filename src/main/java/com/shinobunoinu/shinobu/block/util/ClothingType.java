package com.shinobunoinu.shinobu.block.util;

import net.minecraft.util.StringRepresentable;

public enum ClothingType implements StringRepresentable {
    CLOTH_ONE("cloth_one"),
    CLOTH_TWO("cloth_two"),
    CLOTH_THREE("cloth_three"),
    CLOTH_FOUR("cloth_four"),
    CLOTH_FIVE("cloth_five"),;

    private final String name;

    private ClothingType(String clothName) {
        this.name = clothName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }

    public ClothingType cycle() {
        ClothingType[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
