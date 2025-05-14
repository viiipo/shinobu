package com.shinobunoinu.shinobu.registry;

import com.shinobunoinu.shinobu.Shinobu;
import com.shinobunoinu.shinobu.block.util.ColorType;
import com.shinobunoinu.shinobu.item.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    // 物品注册器（自动对接Forge系统，不要修改这个变量）
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Shinobu.MODID);


    // 默认状态物品
    public static final RegistryObject<Item> SHINOBU_BLOCK_DEFAULT_ITEM =
            ITEMS.register("shinobu_block_default_item",
                    () -> new ShinobuBlockItem(BlockRegistry.SHINOBU_BLOCK.get()));

    // 黑色状态物品
    public static final RegistryObject<Item> SHINOBU_BLOCK_BLACK_ITEM =
            ITEMS.register("shinobu_block_black_item",
                    () -> new ColorVariantBlockItem(
                            BlockRegistry.SHINOBU_BLOCK.get(),
                            ColorType.BLACK,  // 明确传递颜色参数
                            new Item.Properties()
                    ));

    // 粉色状态物品
    public static final RegistryObject<Item> SHINOBU_BLOCK_PINK_ITEM =
            ITEMS.register("shinobu_block_pink_item",
                    () -> new ColorVariantBlockItem(
                            BlockRegistry.SHINOBU_BLOCK.get(),
                            ColorType.PINK,
                            new Item.Properties()
                    ));

}