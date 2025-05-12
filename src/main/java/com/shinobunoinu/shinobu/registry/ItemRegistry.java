package com.shinobunoinu.shinobu.registry;

import com.shinobunoinu.shinobu.Shinobu;
import com.shinobunoinu.shinobu.item.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    // 物品注册器（自动对接Forge系统，不要修改这个变量）
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Shinobu.MODID);


    public static final RegistryObject<Item> Shinobu_BLOCK_ITEM =
            ITEMS.register("shinobu_block_item", () -> new ShinobuBlockItem(BlockRegistry.SHINOBU_BLOCK.get()));


}