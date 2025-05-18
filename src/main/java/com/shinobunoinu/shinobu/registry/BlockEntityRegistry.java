package com.shinobunoinu.shinobu.registry;


import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.shinobunoinu.shinobu.blockentity.ShinobuBlockEntity;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "shinobu");

    public static final RegistryObject<BlockEntityType<ShinobuBlockEntity>> SHINOBU_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("shinobu_block",
                    () -> BlockEntityType.Builder.of(ShinobuBlockEntity::new, BlockRegistry.SHINOBU_BLOCK.get()).build(null));
}