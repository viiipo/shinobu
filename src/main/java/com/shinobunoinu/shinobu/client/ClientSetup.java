package com.shinobunoinu.shinobu.client;

import com.shinobunoinu.shinobu.registry.BlockEntityRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 注册方块实体渲染器
        BlockEntityRenderers.register(
                BlockEntityRegistry.SHINOBU_BLOCK_ENTITY.get(),
                ShinobuRenderer::new
        );
    }

    // 新增模型层注册
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ShinobuRenderer.HAT_LAYER, ShinobuRenderer::createBodyLayer);
    }
}