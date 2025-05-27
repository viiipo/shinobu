package com.shinobunoinu.shinobu.registry;

import com.shinobunoinu.shinobu.Shinobu;
import com.shinobunoinu.shinobu.block.util.ColorType;
import com.shinobunoinu.shinobu.item.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    // 物品注册器（自动对接Forge系统，不要修改这个变量）
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Shinobu.MODID);
    // 可食用甜甜圈（添加食物属性和特殊效果）
    public static final RegistryObject<Item> DONUT = ITEMS.register("donut",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(6)                // 恢复6点饥饿值（3鸡腿）
                            .saturationMod(1.2f)         // 饱和度加成
                            .effect(
                                    () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1), // 给予速度II效果10秒
                                    1.0f                      // 触发概率100%
                            )
                            .alwaysEat()                // 允许满饥饿时食用
                            .build()
                    )
            ));
    // 帽子物品
    public static final RegistryObject<Item> SHINOBU_HAT = ITEMS.register("shinobu_hat",
            () -> new ShinobuHatItem(new ShinobuArmorMaterial(), new Item.Properties()));
    //武器物品
    public static final RegistryObject<Item> KOKOROWATARI = ITEMS.register(
            "kokorowatari",
            () -> new KokorowatariItem()
    );

    // 默认状态物品
    public static final RegistryObject<Item> SHINOBU_BLOCK_DEFAULT_ITEM =
            ITEMS.register("shinobu_block_default_item",
                    () -> new ColorVariantBlockItem(
                            BlockRegistry.SHINOBU_BLOCK.get(),
                            ColorType.DEFAULT,  // 明确传递颜色参数
                            new Item.Properties()
                    ));
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
    public static Item getColorVariantItem(ColorType color) {
        return switch (color) {
            case BLACK -> SHINOBU_BLOCK_BLACK_ITEM.get();
            case PINK -> SHINOBU_BLOCK_PINK_ITEM.get();
            default -> SHINOBU_BLOCK_DEFAULT_ITEM.get();
        };
    }
}