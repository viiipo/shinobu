package com.shinobunoinu.shinobu.item;

import com.shinobunoinu.shinobu.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StrawberryItem extends Item {

    public StrawberryItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState soil = level.getBlockState(pos);

        // 只能种在耕地上
        if (soil.getBlock() instanceof FarmBlock) {
            BlockPos above = pos.above();
            if (level.isEmptyBlock(above)) {
                level.setBlock(above, BlockRegistry.STRAWBERRY_CROP.get().defaultBlockState(), 3);
                level.playSound(null, above, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0f, 1.0f);

                if (!context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }
}
