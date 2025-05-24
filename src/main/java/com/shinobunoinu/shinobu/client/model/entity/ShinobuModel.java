package com.shinobunoinu.shinobu.client.model.entity;

import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

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

    @Override
    public void setCustomAnimations(ShinobuEntity entity, long instanceId, AnimationState<ShinobuEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        // 控制头部旋转
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(data.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(data.netHeadYaw() * ((float) Math.PI / 180F));
        }

        CoreGeoBone hat = getAnimationProcessor().getBone("head_hat");
        if (hat != null) {
            hat.setHidden(!ShinobuEntity.CLIENT_HAS_HAT);

        }
    }
}