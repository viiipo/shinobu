package com.shinobunoinu.shinobu.registry;

import com.shinobunoinu.shinobu.Shinobu;
import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.example.entity.*;
import software.bernie.geckolib.GeckoLib;


public final class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
			Shinobu.MODID);

	public static final RegistryObject<EntityType<ShinobuEntity>> SHINOBU = registerMob("shinobu", ShinobuEntity::new,
			0.7f, 1.3f, 0x1F1F1F, 0x0D0D0D);


	public static <T extends Mob> RegistryObject<EntityType<T>> registerMob(String name, EntityType.EntityFactory<T> entity,
																			float width, float height, int primaryEggColor, int secondaryEggColor) {
		RegistryObject<EntityType<T>> entityType = ENTITIES.register(name,
				() -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));

		return entityType;
	}
}