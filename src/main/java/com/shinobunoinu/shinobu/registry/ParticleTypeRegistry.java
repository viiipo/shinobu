package com.shinobunoinu.shinobu.registry;

import com.shinobunoinu.shinobu.Shinobu;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Shinobu.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleTypeRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
        DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Shinobu.MODID);

    public static final RegistryObject<SimpleParticleType> ZZZ_PARTICLE =
        PARTICLES.register("zzz_particle", () -> new SimpleParticleType(true));
}
