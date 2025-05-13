// ColorType.java
package com.shinobunoinu.shinobu.block.util;

import net.minecraft.util.StringRepresentable;

public enum ColorType implements StringRepresentable {
    DEFAULT("default"),
    BLACK("black"),
    PINK("pink");

    private final String name;

    ColorType(String colorName) {
        this.name = colorName;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}