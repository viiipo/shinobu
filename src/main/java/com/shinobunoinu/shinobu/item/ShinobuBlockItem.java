package com.shinobunoinu.shinobu.item;

import com.shinobunoinu.shinobu.block.ShinobuBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ShinobuBlockItem extends BlockItem {

    //方块物品的构造函数和普通物品没什么两样
    //构造函数，只接受一个实参Block，也就是该物品对应的方块
    public ShinobuBlockItem(Block block) {
        super(block, new Properties()
                // ------------------------- 堆叠数量设置区域 -------------------------
                // 通过.stacksTo()方法设置最大堆叠数量（参数为整数）
                // 不设置时默认最大堆叠64个
                .stacksTo(64) // 设置该物品最大堆叠32个（按需修改数字）

        );
    }

    // 这个方法也不是必要的，如果是建筑方块不需要以下方法
    // 它的功能是更改一些右键功能顺序
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        // 保留原有的Shift+右键放置检查
        if (!context.isSecondaryUseActive() && !context.replacingClickedOnBlock()) {
            return InteractionResult.FAIL;
        }

        // 获取当前手持的物品
        ItemStack stack = context.getItemInHand();

        // 检查是否是颜色变体物品
        if (stack.getItem() instanceof ColorVariantBlockItem colorItem) {
            // 创建新的方块状态并设置颜色
            BlockState stateToPlace = this.getBlock().defaultBlockState()
                    .setValue(ShinobuBlock.COLOR, colorItem.getColor());

            // 执行放置操作
            if (this.placeBlock(context, stateToPlace)) {
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
            }
        }

        return super.place(context);
    }
}
