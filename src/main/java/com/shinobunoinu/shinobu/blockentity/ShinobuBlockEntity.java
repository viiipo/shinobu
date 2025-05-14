// src/main/java/com/shinobunoinu/shinobu/block/ShinobuBlockEntity.java
package com.shinobunoinu.shinobu.blockentity;

import com.shinobunoinu.shinobu.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;

public class ShinobuBlockEntity extends BlockEntity {
    private ItemStack hat = ItemStack.EMPTY;

    public ShinobuBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.SHINOBU_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStack getHat() {
        return hat;
    }

    public void setHat(ItemStack stack) {
        this.hat = stack;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Hat")) {
            hat = ItemStack.of(tag.getCompound("Hat"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Hat", hat.save(new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getTag());
    }
}