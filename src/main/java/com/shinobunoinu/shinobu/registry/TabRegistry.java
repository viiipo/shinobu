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

    /* ------------------------- 主创造标签配置 -------------------------
     * 图标设置：.icon() 设置创造标签的展示图标
     * 标题设置：.title() 设置创造标签的显示名称（需配合翻译文件）
     * 排序规则：默认按注册顺序排列，可通过.displayItems()自定义
     */
    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register(
            "assets/shinobu", // 标签ID（建议保持与MODID一致）
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
            event.accept(new ItemStack(ItemRegistry.DECORATION_HAT.get()));

            // ====================== 批量添加方式 ======================
            // 如果要添加多个物品，可以使用循环：
            // ItemRegistry.ITEMS.getEntries().forEach(item -> {
            //     event.accept(new ItemStack(item.get()));
            // });
        }

        // ▼▼▼ 如需添加其他子标签（示例：食物分类） ▼▼▼
        // else if (event.getTab() == FOOD_TAB.get()) {
        //     event.accept(...);
        // }
    }
}