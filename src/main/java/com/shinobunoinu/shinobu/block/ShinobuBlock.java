package com.shinobunoinu.shinobu.block;

import com.shinobunoinu.shinobu.block.util.ClothingType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.stream.Stream;

/* 这种方块跟普通方块代码区别:
 * 1.继承的类从Block变成了HorizontalDirectionalBlock
 * 2.多了两个方法createBlockStateDefinition和getStateForPlacement
*/
public class ShinobuBlock extends HorizontalDirectionalBlock {

    public ShinobuBlock() {
        super(Properties.of()
                // ------------------------- 面渲染 -------------------------
                // 如果没有这个参数，那么方块只会渲染玩家看到它的三个面，非完整方块必加，不然会直接透视,你可能会问，那我不管啥方块都加上不就完了，还真是，这个方法存在是为了优化性能的
                .noOcclusion()
                // ------------------------- 声音 -------------------------
                // SoundType类是MC原版方块的音效库，偷懒的话直接从这调用就行，比如这里用的羊毛声音模板，把.WOOL删了重新打.符号，会提示你有哪些选择
                // 自定义音效以后再讲
                .sound(SoundType.WOOL));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CLOTHING_TYPE, ClothingType.CLOTH_ONE)
        );
    }

    // 这个方法就是给方块添加了一个方块状态FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        pBuilder.add(CLOTHING_TYPE);
    }

    // 这个方法是让玩家放置该方块的时候设定状态为FACING，FACING有东南西北四个值，context.getHorizontalDirection()这个参数就是玩家面朝方向，.getOpposite()把前面的方向取反，这样方块就能面朝玩家了
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    public static final VoxelShape SHAPE = Stream.of(
                    // 这里每一个Block.box(8, 0, 7, 9, 11, 8)都是一个bb模型的cube，有多少个cube就得写多少行
                    // 前面三个数字和后面三个数字是这个正方体对角线两个点的坐标
                    // 如果想要碰撞体积贴合模型，直接从模型json文件里面把每个方块元素copy过来
            /*
            浏览一下模型json，里面那个elements后面每一个大括号是一个cube，from三个数字抄前面，to后面三个数字抄后面
			"from": [8, 0, 7],
			"to": [9, 11, 8],
            */
                    // 这个数字最大不能超过16，超过16就改为16
                    Block.box(5, 0, 4, 11, 12, 12)

            )
            //后面这一坨直接复制就完了
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    // 这个方法直接复制就完了，用来把创建好的SHAPE给组装上
    @Override
    public VoxelShape getShape(BlockState State, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }


    // 新增属性
    public static final EnumProperty<ClothingType> CLOTHING_TYPE = EnumProperty.create("cloth", ClothingType.class);

    // 保留原有的面朝玩家逻辑

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (level instanceof ServerLevel serverLevel) {
            Direction facing = state.getValue(ShinobuBlock.FACING);
            ClothingType nextType = state.getValue(CLOTHING_TYPE).cycle();
            // 创建新状态：保留朝向，切换激活状态
            BlockState newState = state
                    .setValue(ShinobuBlock.FACING, facing) // 确保朝向不变
                    .setValue(ShinobuBlock.CLOTHING_TYPE, nextType);
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }
}
