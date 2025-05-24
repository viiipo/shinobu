package com.shinobunoinu.shinobu.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shinobunoinu.shinobu.client.model.entity.ShinobuModel;
import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShinobuRenderer extends GeoEntityRenderer<ShinobuEntity> {
    public ShinobuRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShinobuModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public void render(ShinobuEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // ✅ 每帧从实体同步状态 → 客户端静态变量（供模型判断用）
        ShinobuEntity.CLIENT_HAS_HAT = entity.hasHeadArmor();
        System.out.println("[DEBUG] ShinobuEntity.CLIENT_HAS_HAT = " + ShinobuEntity.CLIENT_HAS_HAT);

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }}