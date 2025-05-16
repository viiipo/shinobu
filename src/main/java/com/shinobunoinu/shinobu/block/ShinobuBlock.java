package com.shinobunoinu.shinobu.block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.shinobunoinu.shinobu.block.util.ColorType;
import com.shinobunoinu.shinobu.block.util.Gesture;
import com.shinobunoinu.shinobu.blockentity.DecoratedBlockEntity;
import com.shinobunoinu.shinobu.item.DecorationItem;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ShinobuBlock extends HorizontalDirectionalBlock {

    // 状态属性
    public static final EnumProperty<Gesture> GESTURE = EnumProperty.create("gesture", Gesture.class);
    public static final EnumProperty<ColorType> COLOR = EnumProperty.create("color", ColorType.class);

    // 碰撞箱定义
    private static final VoxelShape STAND_SHAPE = Block.box(5, 0, 4, 11, 15, 12);
    private static final VoxelShape SIT_SHAPE = Block.box(5, 0, 4, 11, 12, 12);
    private static final VoxelShape LIE_SHAPE = Block.box(3, 0, 3, 13, 7, 13);

    public ShinobuBlock() {
        super(Properties.of()
                .noOcclusion()
                .sound(SoundType.WOOL)
                .requiresCorrectToolForDrops());
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(GESTURE, Gesture.SIT)
                .setValue(COLOR, ColorType.DEFAULT));
    }

    // 注册状态属性
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, GESTURE, COLOR);
    }

    // 玩家放置方块时的方向
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // 碰撞箱逻辑
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(GESTURE)) {
            case SIT -> SIT_SHAPE;
            case LIE -> LIE_SHAPE;
            default -> STAND_SHAPE;
        };
    }

    // 综合交互逻辑
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);

        // 1. 处理装饰物放置
        if (stack.getItem() instanceof DecorationItem) {
            return handleDecoration(level, pos, player, stack);
        }

        // 2. 处理Shift+右键取下装饰物
        if (player.isShiftKeyDown() && stack.isEmpty()) {
            return removeDecoration(level, pos, player);
        }

        // 3. 处理染料染色
        if (stack.getItem() instanceof DyeItem dye) {
            return handleDye(state, level, pos, player, dye);
        }

        // 4. 处理空手切换姿势
        if (stack.isEmpty()) {
            return cycleGesture(state, level, pos);
        }

        return super.use(state, level, pos, player, hand, hit);
    }   // 装饰物放置逻辑
    private InteractionResult handleDecoration(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DecoratedBlockEntity decoratedBlockEntity) {
                if (!decoratedBlockEntity.hasHat()) {
                    decoratedBlockEntity.setHat(true);
                    stack.shrink(1);
                    level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // 取下装饰物逻辑
    private InteractionResult removeDecoration(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DecoratedBlockEntity decoratedBlockEntity) {
                ItemStack hat = decoratedBlockEntity.removeHat();
                if (!hat.isEmpty()) {
                    player.addItem(hat);
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // 染料染色逻辑
    private InteractionResult handleDye(BlockState state, Level level, BlockPos pos, Player player, DyeItem dye) {
        ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND); // 获取主手持有的物品堆栈

        ColorType newColor = switch (dye.getDyeColor()) {
            case WHITE -> ColorType.DEFAULT;
            case BLACK -> ColorType.BLACK;
            case PINK -> ColorType.PINK;
            default -> null;
        };

        if (newColor != null && state.getValue(COLOR) != newColor) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(COLOR, newColor), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    heldStack.shrink(1); // 使用正确获取的 heldStack
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    // 切换姿势逻辑
    private InteractionResult cycleGesture(BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide) {
            Gesture newGesture = state.getValue(GESTURE).cycle();
            level.setBlock(pos, state.setValue(GESTURE, newGesture), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // 掉落物逻辑（方案一：通过BLOCK_ENTITY参数）
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();

        // 添加基础方块
        drops.add(createBaseStack(state));

        // 添加帽子
        BlockEntity be = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof DecoratedBlockEntity decoratedBlockEntity) {
            if (decoratedBlockEntity.hasHat()) {
                drops.add(new ItemStack(ItemRegistry.DECORATION_HAT.get()));
            }
        }

        return drops;
    }

    // 创建基础方块掉落物
    private ItemStack createBaseStack(BlockState state) {
        ItemStack stack = new ItemStack(getColorItem(state.getValue(COLOR)));
        CompoundTag tag = new CompoundTag();
        tag.putString("color", state.getValue(COLOR).getSerializedName());
        stack.setTag(tag);
        return stack;
    }

    // 颜色到物品的映射
    private Item getColorItem(ColorType color) {
        return switch (color) {
            case BLACK -> ItemRegistry.SHINOBU_BLOCK_BLACK_ITEM.get();
            case PINK -> ItemRegistry.SHINOBU_BLOCK_PINK_ITEM.get();
            default -> ItemRegistry.SHINOBU_BLOCK_DEFAULT_ITEM.get();
        };
    }
}