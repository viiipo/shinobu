package com.shinobunoinu.shinobu.client.model;

import com.shinobunoinu.shinobu.item.KokorowatariItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KokorowatariModel extends GeoModel<KokorowatariItem> {

    @Override
    public ResourceLocation getModelResource(KokorowatariItem animatable) {
        return new ResourceLocation("shinobu", "geo/kokorowatari.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KokorowatariItem animatable) {
        return new ResourceLocation("shinobu", "textures/item/kokorowatari.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KokorowatariItem animatable) {
        return new ResourceLocation("shinobu", "animations/kokorowatari.animation.json"); // 可保留或空动画
    }
}
