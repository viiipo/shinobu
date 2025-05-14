package com.shinobunoinu.shinobu.item;

import com.shinobunoinu.shinobu.block.ShinobuBlock;
import com.shinobunoinu.shinobu.block.util.ColorType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class ColorVariantBlockItem extends BlockItem {
    private final ColorType color;

    // 主构造函数
    public ColorVariantBlockItem(Block block, ColorType color, Properties properties) {
        super(block, properties);
        this.color = color;
    }

    // 新增辅助构造函数（自动填充默认Properties）
    public ColorVariantBlockItem(Block block, ColorType color) {
        this(block, color, new Item.Properties()); // 默认属性
    }
    // 新增颜色获取方法
    public ColorType getColor() {
        return this.color;
    }
    // 重写放置逻辑
    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        // 创建带有颜色的方块状态
        BlockState coloredState = state.setValue(ShinobuBlock.COLOR, this.color);
        return super.placeBlock(context, coloredState);
    }

    // 新增：同步客户端预览效果
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Color: " + this.color.name()));
    }

}