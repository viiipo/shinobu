package com.shinobunoinu.shinobu.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shinobunoinu.shinobu.block.ShinobuBlock;
import com.shinobunoinu.shinobu.block.util.Gesture;
import com.shinobunoinu.shinobu.blockentity.DecoratedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class DecoratedBlockRenderer implements BlockEntityRenderer<DecoratedBlockEntity> {
    private static final ModelResourceLocation HAT_MODEL =
            new ModelResourceLocation(
                    new ResourceLocation("shinobu", "decorations/hat"), // 正确创建 ResourceLocation
                    "inventory" // 需要指定变体参数（通常为 "inventory" 表示物品模型）
            );

    public DecoratedBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(DecoratedBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!blockEntity.hasHat()) return;

        poseStack.pushPose();
        applyGestureTransform(blockEntity.getBlockState().getValue(ShinobuBlock.GESTURE), poseStack);

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(HAT_MODEL);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                buffer.getBuffer(RenderType.cutout()),
                null,
                model,
                1, 1, 1,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }

    private void applyGestureTransform(Gesture gesture, PoseStack poseStack) {
        switch (gesture) {
            case STAND -> poseStack.translate(0.5, 1.2, 0.5);
            case SIT -> {
                poseStack.translate(0.5, 0.8, 0.5);
                poseStack.scale(0.8F, 0.8F, 0.8F);
            }
            case LIE -> {
                poseStack.translate(0.5, 0.4, 0.8);
                poseStack.mulPose(Axis.XP.rotationDegrees(90)); // 修正后的旋转轴
                poseStack.scale(0.7F, 0.7F, 0.7F);
            }
        }
    }
}