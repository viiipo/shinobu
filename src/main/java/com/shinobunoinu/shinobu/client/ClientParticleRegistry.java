package com.shinobunoinu.shinobu.client;

import com.shinobunoinu.shinobu.client.particle.ZzzParticle;
import com.shinobunoinu.shinobu.registry.ParticleTypeRegistry;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "shinobu", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientParticleRegistry {

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
            ParticleTypeRegistry.ZZZ_PARTICLE.get(),
            ZzzParticle.Provider::new
        );
    }
}
