package com.shinobunoinu.shinobu.client.model.entity;

import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShinobuModel extends GeoModel<ShinobuEntity> {
    @Override
    public ResourceLocation getModelResource(ShinobuEntity entity) {
        return new ResourceLocation("shinobu", "geo/shinobu.geo.json");


    }

    @Override
    public ResourceLocation getTextureResource(ShinobuEntity entity) {
        return new ResourceLocation("shinobu", "textures/entity/shinobu.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShinobuEntity entity) {
        return new ResourceLocation("shinobu", "animations/shinobu.animation.json");

    }
}
