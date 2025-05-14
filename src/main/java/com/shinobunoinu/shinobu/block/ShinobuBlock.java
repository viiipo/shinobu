package com.shinobunoinu.shinobu.block;

import com.shinobunoinu.shinobu.block.util.ColorType;
import com.shinobunoinu.shinobu.block.util.Gesture;
import com.shinobunoinu.shinobu.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collections;
import java.util.List;
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
        registerDefaultState(this.stateDefinition.any()
                .setValue(GESTURE, Gesture.SIT)
                .setValue(COLOR, ColorType.DEFAULT));
    }

    // 这个方法就是给方块添加了一个方块状态FACING
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        pBuilder.add(GESTURE);
        pBuilder.add(COLOR);
    }

    // 这个方法是让玩家放置该方块的时候设定状态为FACING，FACING有东南西北四个值，context.getHorizontalDirection()这个参数就是玩家面朝方向，.getOpposite()把前面的方向取反，这样方块就能面朝玩家了
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    private static final VoxelShape STAND_SHAPE = Block.box(5, 0, 4, 11, 15, 12); // 完整方块
    private static final VoxelShape SIT_SHAPE = Block.box(5, 0, 4, 11, 12, 12);   // 半高方块
    private static final VoxelShape LIE_SHAPE = Block.box(3, 0, 3, 13, 7, 13);   // 更矮的碰撞箱

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // 根据当前状态返回对应的碰撞箱
        switch (state.getValue(GESTURE)) {
            case SIT:
                return SIT_SHAPE;
            case LIE:
                return LIE_SHAPE;
            case STAND:
            default:
                return STAND_SHAPE;
        }
    }


    // 新增属性
    public static final EnumProperty<Gesture> GESTURE = EnumProperty.create("gesture", Gesture.class);
    // 添加颜色属性
    public static final EnumProperty<ColorType> COLOR = EnumProperty.create("color", ColorType.class);

    // 保留原有的面朝玩家逻辑

    @Override
    // 合并后的交互逻辑

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 优先处理染料交互
        if (itemStack.getItem() instanceof DyeItem dye) {
            ColorType newColor = switch (dye.getDyeColor()) {
                case WHITE -> ColorType.DEFAULT;
                case BLACK -> ColorType.BLACK;
                case PINK -> ColorType.PINK;
                default -> null;
            };

            if (newColor != null && state.getValue(COLOR) != newColor) {
                if (!world.isClientSide) {
                    world.setBlock(pos, state.setValue(COLOR, newColor), Block.UPDATE_ALL);
                    world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        // 空手交互处理原有姿势切换
        else if (itemStack.isEmpty()) {
            if (!world.isClientSide) {
                BlockState newState = state.setValue(GESTURE, state.getValue(GESTURE).cycle());
                world.setBlock(pos, newState, Block.UPDATE_ALL);
                world.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return super.use(state, world, pos, player, hand, hit);
    }
    // 关键方法：定义掉落物逻辑
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        // 创建物品堆栈
        ItemStack stack = new ItemStack(ItemRegistry.Shinobu_BLOCK_ITEM.get());

        // 保存颜色状态到NBT
        CompoundTag tag = new CompoundTag();
        tag.putString("color", state.getValue(COLOR).getSerializedName());
        stack.setTag(tag);

        return Collections.singletonList(stack);
    }
}

