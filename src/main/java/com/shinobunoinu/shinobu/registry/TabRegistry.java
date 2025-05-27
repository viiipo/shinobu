package com.shinobunoinu.shinobu.registry;

import com.shinobunoinu.shinobu.Shinobu;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Shinobu.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabRegistry {
    // 注册表初始化（不要修改）
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Shinobu.MODID);


    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register(
            "shinobu", // 标签ID（建议保持与MODID一致）
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ItemRegistry.SHINOBU_BLOCK_DEFAULT_ITEM.get()))
                    .title(Component.translatable("shinobu")) // ▶▶▶ 修改翻译键
                    .build()
    );

    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 物品添加区域 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 判断是否为我们的主标签
        if (event.getTab() == MAIN.get()) {
            // ====================== 单个物品添加方式 ======================
            // 格式：event.accept(new ItemStack(物品注册项.get()));

            event.accept(new ItemStack(ItemRegistry.SHINOBU_BLOCK_DEFAULT_ITEM.get()));
            event.accept(new ItemStack(ItemRegistry.DONUT.get()));
            event.accept(new ItemStack(ItemRegistry.SHINOBU_HAT.get()));
            event.accept(new ItemStack(ItemRegistry.KOKOROWATARI.get()));
            event.accept(new ItemStack(ItemRegistry.STRAWBERRY.get()));

        }
    }}