package com.shinobunoinu.shinobu;

import com.shinobunoinu.shinobu.client.particle.ZzzParticle;
import com.shinobunoinu.shinobu.client.renderer.entity.ShinobuRenderer;
import com.shinobunoinu.shinobu.registry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;



import com.shinobunoinu.shinobu.entity.ShinobuEntity;

import net.minecraft.client.particle.ParticleEngine;

@Mod(Shinobu.MODID)
public class Shinobu {
    public static final String MODID = "shinobu";

    public Shinobu() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册内容
        ItemRegistry.ITEMS.register(modBus);
        BlockRegistry.BLOCKS.register(modBus);
        EntityRegistry.ENTITIES.register(modBus);
        TabRegistry.TABS.register(modBus);
        ParticleTypeRegistry.PARTICLES.register(modBus);

        // 初始化 GeckoLib
        GeckoLib.initialize();

        // 注册监听器
        modBus.addListener(this::onAttributeCreate);
        modBus.addListener(this::onClientSetup);
    }

    private void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.SHINOBU.get(), ShinobuEntity.createAttributes().build());
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // 注册实体渲染器
        EntityRenderers.register(EntityRegistry.SHINOBU.get(), ShinobuRenderer::new);


    }
}


