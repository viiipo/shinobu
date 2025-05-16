package com.shinobunoinu.shinobu.registry;

import com.shinobunoinu.shinobu.Shinobu;
import com.shinobunoinu.shinobu.blockentity.DecoratedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Shinobu.MODID);

    public static final RegistryObject<BlockEntityType<DecoratedBlockEntity>> DECORATED_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("decorated_block",
                    () -> BlockEntityType.Builder.of(
                            DecoratedBlockEntity::new,
                            BlockRegistry.SHINOBU_BLOCK.get()
                    ).build(null)
            );
}