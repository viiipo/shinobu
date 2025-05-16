package com.shinobunoinu.shinobu.blockentity;

import com.shinobunoinu.shinobu.registry.BlockEntityRegistry;
import com.shinobunoinu.shinobu.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratedBlockEntity extends BlockEntity {
    private boolean hasHat = false;

    public DecoratedBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.DECORATED_BLOCK_ENTITY.get(), pos, state);
    }

    // 设置帽子状态
    public void setHat(boolean hasHat) {
        this.hasHat = hasHat;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    // 移除帽子并返回物品
    public ItemStack removeHat() {
        if (!hasHat) return ItemStack.EMPTY;
        hasHat = false;
        setChanged();
        return new ItemStack(ItemRegistry.DECORATION_HAT.get());
    }

    public boolean hasHat() {
        return hasHat;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putBoolean("hasHat", hasHat);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        hasHat = tag.getBoolean("hasHat"); // 确保键名一致
    }
}