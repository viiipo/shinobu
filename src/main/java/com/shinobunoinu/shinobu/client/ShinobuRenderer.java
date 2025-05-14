// 文件位置：src/main/java/com/shinobunoinu/shinobu/client/ShinobuRenderer.java
package com.shinobunoinu.shinobu.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.shinobunoinu.shinobu.block.ShinobuBlock;
import com.shinobunoinu.shinobu.blockentity.ShinobuBlockEntity;
import com.shinobunoinu.shinobu.block.util.Gesture;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class ShinobuRenderer implements BlockEntityRenderer<ShinobuBlockEntity> {
    // 修改模型层定义方式
    public static final ModelLayerLocation HAT_LAYER =
            new ModelLayerLocation(new ResourceLocation("shinobu", "hat_model"), "main");

    // 贴图路径
    private static final ResourceLocation HAT_TEXTURE =
            new ResourceLocation("shinobu", "textures/block/hat.png");

    private final ModelPart hatModel;

    public ShinobuRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(HAT_LAYER);
        this.hatModel = root.getChild("group"); // 对应模型中的groups名称
    }

    // 模型注册方法
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        // 根据Blockbench模型自动生成（不要手动修改）
        parts.addOrReplaceChild("group", CubeListBuilder.create()
                        // 对应模型中的每个cube数据
                        .texOffs(0, 0).addBox(3.6F, 0.3F, 4.5F, 8.7F, 2.7F, 6.8F, new CubeDeformation(0.0F)) // 第一个cube
                        .texOffs(0, 9).addBox(1.4F, 0.3F, 2.3F, 13.2F, 0.0F, 11.3F, new CubeDeformation(0.0F)) // 第二个cube
                // ... 添加所有13个cube定义（此处需根据实际模型补充完整）
                ,
                PartPose.offsetAndRotation(-8.0F, 24.0F, -8.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64); // 匹配texture_size
    }

    @Override
    public void render(ShinobuBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be.getHat().isEmpty()) return;

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(ShinobuBlock.FACING);
        Gesture gesture = state.getValue(ShinobuBlock.GESTURE);

        poseStack.pushPose();
        applyModelTransforms(poseStack, facing, gesture);

        VertexConsumer vertex = buffer.getBuffer(RenderType.entitySolid(HAT_TEXTURE));
        hatModel.render(poseStack, vertex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void applyModelTransforms(PoseStack poseStack, Direction facing, Gesture gesture) {
        // 基础变换
        poseStack.translate(0.5, 1.5, 0.5); // 调整模型中心点
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.scale(0.8F, 0.8F, 0.8F);

        // 根据姿势调整
        switch (gesture) {
            case SIT -> {
                poseStack.translate(0, -0.35, 0.1);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
            }
            case LIE -> {
                poseStack.translate(0, -0.5, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
            default -> poseStack.translate(0, 0.1, -0.05);
        }
    }
}