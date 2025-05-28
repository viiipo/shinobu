package com.shinobunoinu.shinobu.block;

import com.shinobunoinu.shinobu.registry.BlockRegistry;
import com.shinobunoinu.shinobu.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;

public class StrawberryCropBlock extends CropBlock {

    public static final IntegerProperty VINE_AGE = BlockStateProperties.AGE_4;
    protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D),
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D)
    };

    public StrawberryCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(getAgeProperty(), 0));
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return VINE_AGE;
    }

    @Override
    public int getMaxAge() {
        return 4;
    }

    // 🍓 草莓就是种子
    @Override
    protected net.minecraft.world.item.Item getBaseSeedId() {
        return ItemRegistry.STRAWBERRY.get();
    }

    // ✅ 骨粉成功率 60%
    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.6f;
    }

    // ✅ 骨粉每次只 +1 阶段
    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = getAge(state);
        if (age < getMaxAge()) {
            level.setBlock(pos, state.setValue(getAgeProperty(), age + 1), 2);
        }
    }

    // ✅ 成熟时左键破坏掉落草莓 2~3 个，未成熟时无掉落
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (getAge(state) >= getMaxAge()) {
            int count = 2 + builder.getLevel().random.nextInt(2); // 2~3
            return Collections.singletonList(new ItemStack(ItemRegistry.STRAWBERRY.get(), count));
        }
        return Collections.emptyList();
    }

    // ✅ 成熟时右键原版锄头收割，不破坏，掉落草莓并重置阶段
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && getAge(state) >= getMaxAge()) {
            ItemStack held = player.getItemInHand(hand);

            // 使用原版 Hoe（锄头）右键
            if (held.getItem() instanceof HoeItem) {
                int count = 2 + level.random.nextInt(2);
                popResource(level, pos, new ItemStack(ItemRegistry.STRAWBERRY.get(), count));

                // 播放粒子和音效
                level.levelEvent(2001, pos, Block.getId(state));
                level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);

                // 重置为生长阶段 0
                level.setBlock(pos, state.setValue(getAgeProperty(), 0), 2);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    // ✅ 注册作物生长属性
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getAgeProperty());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(getAgeProperty())];
    }
}
