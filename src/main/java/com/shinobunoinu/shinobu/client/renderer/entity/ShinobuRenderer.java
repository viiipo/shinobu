package com.shinobunoinu.shinobu.client.renderer.entity;

import com.shinobunoinu.shinobu.client.model.entity.ShinobuModel;
import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShinobuRenderer extends GeoEntityRenderer<ShinobuEntity> {
    public ShinobuRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShinobuModel());
        this.shadowRadius = 0.4F;
    }
}
