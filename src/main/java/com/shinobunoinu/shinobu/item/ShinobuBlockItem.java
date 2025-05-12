package com.shinobunoinu.shinobu.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

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
        /* ------------------------- java小课堂 -------------------------
        * 在java里'!'表示'非'，将后面的逻辑判断颠倒，在这里它颠倒了context.isSecondaryUseActive()
        * 布尔值是计算机科学中的一个基本概念，表示“真”（True）或“假”（False）
        * &&表示和，也是用于布尔值的逻辑运算符，意味前后值都为True代码才会执行if大括号里的内容
        */
        // context.isSecondaryUseActive() 这个方法返回一个布尔值，如果玩家使用潜行右键会返回true，有!反转就是潜行右键返回false，游戏里就是如果潜行右键就不会执行{}中的的return InteractionResult.FAIL使放置成功
        // 设置这个判断目的在于让玩家手持该方块shift+右键时执行放置功能而不是其他功能比如说吃
        // context.replacingClickedOnBlock() 在目标方块有'可替换'标签的时候才让放置，草、水方块都有这个标签，直接对着它们放置会替换掉
        if (!context.isSecondaryUseActive() && !context.replacingClickedOnBlock()) {
            return InteractionResult.FAIL;
        }
        return super.place(context);
    }
}
