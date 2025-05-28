package com.shinobunoinu.shinobu.registry;

import com.shinobunoinu.shinobu.Shinobu;
import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Shinobu.MODID);

    public static final RegistryObject<EntityType<ShinobuEntity>> SHINOBU =
            ENTITIES.register("shinobu", () ->
                    EntityType.Builder.<ShinobuEntity>of(ShinobuEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.0f) // 实体宽高
                            .build(new ResourceLocation(Shinobu.MODID, "shinobu").toString()));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SHINOBU.get(), ShinobuEntity.createAttributes().build());
    }
}
