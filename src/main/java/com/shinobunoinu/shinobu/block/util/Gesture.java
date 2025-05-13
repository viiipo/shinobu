package com.shinobunoinu.shinobu.block.util;

import net.minecraft.util.StringRepresentable;

public enum Gesture implements StringRepresentable {
    STAND("stand"),
    SIT("sit"),
    LIE("lie");

    private final String name;

    Gesture(String gestureName) {
        this.name = gestureName;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Gesture cycle() {
        Gesture[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}