/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 */
package com.sk89q.worldedit.neoforge.internal;

import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.internal.wna.WorldNativeAccess;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.internal.ExtendedChunk;
import com.sk89q.worldedit.neoforge.internal.NBTConverter;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.block.BlockState;
import java.lang.ref.WeakReference;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.enginehub.linbus.tree.LinCompoundTag;

public class NeoForgeWorldNativeAccess
implements WorldNativeAccess<LevelChunk, net.minecraft.world.level.block.state.BlockState, BlockPos> {
    private static final int UPDATE = 1;
    private static final int NOTIFY = 2;
    private final WeakReference<ServerLevel> world;
    private SideEffectSet sideEffectSet;

    public NeoForgeWorldNativeAccess(WeakReference<ServerLevel> world) {
        this.world = world;
    }

    private ServerLevel getWorld() {
        return Objects.requireNonNull((ServerLevel)this.world.get(), "The reference to the world was lost");
    }

    @Override
    public void setCurrentSideEffectSet(SideEffectSet sideEffectSet) {
        this.sideEffectSet = sideEffectSet;
    }

    @Override
    public LevelChunk getChunk(int x, int z) {
        return this.getWorld().getChunk(x, z);
    }

    @Override
    public net.minecraft.world.level.block.state.BlockState toNative(BlockState state) {
        int stateId = BlockStateIdAccess.getBlockStateId(state);
        return BlockStateIdAccess.isValidInternalId(stateId) ? Block.stateById((int)stateId) : NeoForgeAdapter.adapt(state);
    }

    @Override
    public net.minecraft.world.level.block.state.BlockState getBlockState(LevelChunk chunk, BlockPos position) {
        return chunk.getBlockState(position);
    }

    @Override
    @Nullable
    public net.minecraft.world.level.block.state.BlockState setBlockState(LevelChunk chunk, BlockPos position, net.minecraft.world.level.block.state.BlockState state) {
        if (chunk instanceof ExtendedChunk) {
            return ((ExtendedChunk)chunk).setBlockState(position, state, false, this.sideEffectSet.shouldApply(SideEffect.UPDATE));
        }
        return chunk.setBlockState(position, state, false);
    }

    @Override
    public net.minecraft.world.level.block.state.BlockState getValidBlockForPosition(net.minecraft.world.level.block.state.BlockState block, BlockPos position) {
        return Block.updateFromNeighbourShapes((net.minecraft.world.level.block.state.BlockState)block, (LevelAccessor)this.getWorld(), (BlockPos)position);
    }

    @Override
    public BlockPos getPosition(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Override
    public void updateLightingForBlock(BlockPos position) {
        this.getWorld().getChunkSource().getLightEngine().checkBlock(position);
    }

    @Override
    public boolean updateTileEntity(BlockPos position, LinCompoundTag tag) {
        CompoundTag nativeTag = NBTConverter.toNative(tag);
        ServerLevel level = this.getWorld();
        BlockEntity tileEntity = level.getChunkAt(position).getBlockEntity(position);
        if (tileEntity == null) {
            return false;
        }
        tileEntity.loadWithComponents(nativeTag, (HolderLookup.Provider)level.registryAccess());
        tileEntity.setChanged();
        return true;
    }

    @Override
    public void notifyBlockUpdate(LevelChunk chunk, BlockPos position, net.minecraft.world.level.block.state.BlockState oldState, net.minecraft.world.level.block.state.BlockState newState) {
        if (chunk.getSections()[this.getWorld().getSectionIndex(position.getY())] != null) {
            this.getWorld().sendBlockUpdated(position, oldState, newState, 3);
        }
    }

    @Override
    public boolean isChunkTicking(LevelChunk chunk) {
        return chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING);
    }

    @Override
    public void markBlockChanged(LevelChunk chunk, BlockPos position) {
        if (chunk.getSections()[this.getWorld().getSectionIndex(position.getY())] != null) {
            this.getWorld().getChunkSource().blockChanged(position);
        }
    }

    @Override
    public void notifyNeighbors(BlockPos pos, net.minecraft.world.level.block.state.BlockState oldState, net.minecraft.world.level.block.state.BlockState newState) {
        ServerLevel world = this.getWorld();
        if (this.sideEffectSet.shouldApply(SideEffect.EVENTS)) {
            world.updateNeighborsAt(pos, oldState.getBlock());
        } else {
            Block block = oldState.getBlock();
            world.neighborChanged(pos.west(), block, pos);
            world.neighborChanged(pos.east(), block, pos);
            world.neighborChanged(pos.below(), block, pos);
            world.neighborChanged(pos.above(), block, pos);
            world.neighborChanged(pos.north(), block, pos);
            world.neighborChanged(pos.south(), block, pos);
        }
        if (newState.hasAnalogOutputSignal()) {
            world.updateNeighbourForOutputSignal(pos, newState.getBlock());
        }
    }

    @Override
    public void updateBlock(BlockPos pos, net.minecraft.world.level.block.state.BlockState oldState, net.minecraft.world.level.block.state.BlockState newState) {
        ServerLevel world = this.getWorld();
        newState.onPlace((Level)world, pos, oldState, false);
    }

    @Override
    public void updateNeighbors(BlockPos pos, net.minecraft.world.level.block.state.BlockState oldState, net.minecraft.world.level.block.state.BlockState newState, int recursionLimit) {
        ServerLevel world = this.getWorld();
        oldState.updateIndirectNeighbourShapes((LevelAccessor)world, pos, 2, recursionLimit);
        newState.updateNeighbourShapes((LevelAccessor)world, pos, 2, recursionLimit);
        newState.updateIndirectNeighbourShapes((LevelAccessor)world, pos, 2, recursionLimit);
    }

    @Override
    public void onBlockStateChange(BlockPos pos, net.minecraft.world.level.block.state.BlockState oldState, net.minecraft.world.level.block.state.BlockState newState) {
        this.getWorld().onBlockStateChange(pos, oldState, newState);
        newState.onBlockStateChange((LevelReader)this.getWorld(), pos, oldState);
    }
}

