/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPosition
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.WorldServer
 *  net.minecraft.world.level.GeneratorAccess
 *  net.minecraft.world.level.World
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.TileEntity
 *  net.minecraft.world.level.block.state.IBlockData
 *  net.minecraft.world.level.chunk.Chunk
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.craftbukkit.v1_20_R4.CraftWorld
 *  org.bukkit.craftbukkit.v1_20_R4.block.data.CraftBlockData
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.BlockPhysicsEvent
 */
package com.sk89q.worldedit.bukkit.adapter.impl.v1_20_R4;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_20_R4.PaperweightAdapter;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.internal.wna.WorldNativeAccess;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.block.BlockState;
import java.lang.ref.WeakReference;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R4.block.data.CraftBlockData;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.enginehub.linbus.tree.LinCompoundTag;

public class PaperweightWorldNativeAccess
implements WorldNativeAccess<Chunk, IBlockData, BlockPosition> {
    private static final int UPDATE = 1;
    private static final int NOTIFY = 2;
    private final PaperweightAdapter adapter;
    private final WeakReference<WorldServer> world;
    private SideEffectSet sideEffectSet;

    public PaperweightWorldNativeAccess(PaperweightAdapter adapter, WeakReference<WorldServer> world) {
        this.adapter = adapter;
        this.world = world;
    }

    private WorldServer getWorld() {
        return Objects.requireNonNull((WorldServer)this.world.get(), "The reference to the world was lost");
    }

    @Override
    public void setCurrentSideEffectSet(SideEffectSet sideEffectSet) {
        this.sideEffectSet = sideEffectSet;
    }

    @Override
    public Chunk getChunk(int x, int z) {
        return this.getWorld().d(x, z);
    }

    @Override
    public IBlockData toNative(BlockState state) {
        int stateId = BlockStateIdAccess.getBlockStateId(state);
        return BlockStateIdAccess.isValidInternalId(stateId) ? Block.a((int)stateId) : ((CraftBlockData)BukkitAdapter.adapt(state)).getState();
    }

    @Override
    public IBlockData getBlockState(Chunk chunk, BlockPosition position) {
        return chunk.a_(position);
    }

    @Override
    @Nullable
    public IBlockData setBlockState(Chunk chunk, BlockPosition position, IBlockData state) {
        return chunk.setBlockState(position, state, false, this.sideEffectSet.shouldApply(SideEffect.UPDATE));
    }

    @Override
    public IBlockData getValidBlockForPosition(IBlockData block, BlockPosition position) {
        return Block.b((IBlockData)block, (GeneratorAccess)this.getWorld(), (BlockPosition)position);
    }

    @Override
    public BlockPosition getPosition(int x, int y, int z) {
        return new BlockPosition(x, y, z);
    }

    @Override
    public void updateLightingForBlock(BlockPosition position) {
        this.getWorld().l().a().a(position);
    }

    @Override
    public boolean updateTileEntity(BlockPosition position, LinCompoundTag tag) {
        TileEntity tileEntity = this.getWorld().c_(position);
        if (tileEntity == null) {
            return false;
        }
        NBTBase nativeTag = this.adapter.fromNative(tag);
        PaperweightAdapter.readTagIntoTileEntity((NBTTagCompound)nativeTag, tileEntity);
        return true;
    }

    @Override
    public void notifyBlockUpdate(Chunk chunk, BlockPosition position, IBlockData oldState, IBlockData newState) {
        if (chunk.d()[this.getWorld().e(position.v())] != null) {
            this.getWorld().a(position, oldState, newState, 3);
        }
    }

    @Override
    public boolean isChunkTicking(Chunk chunk) {
        return chunk.D().a(FullChunkStatus.c);
    }

    @Override
    public void markBlockChanged(Chunk chunk, BlockPosition position) {
        if (chunk.d()[this.getWorld().e(position.v())] != null) {
            this.getWorld().l().a(position);
        }
    }

    @Override
    public void notifyNeighbors(BlockPosition pos, IBlockData oldState, IBlockData newState) {
        WorldServer world = this.getWorld();
        if (this.sideEffectSet.shouldApply(SideEffect.EVENTS)) {
            world.a(pos, oldState.b());
        } else {
            Block block = oldState.b();
            this.fireNeighborChanged(pos, world, block, pos.g());
            this.fireNeighborChanged(pos, world, block, pos.h());
            this.fireNeighborChanged(pos, world, block, pos.d());
            this.fireNeighborChanged(pos, world, block, pos.c());
            this.fireNeighborChanged(pos, world, block, pos.e());
            this.fireNeighborChanged(pos, world, block, pos.f());
        }
        if (newState.n()) {
            world.c(pos, newState.b());
        }
    }

    @Override
    public void updateBlock(BlockPosition pos, IBlockData oldState, IBlockData newState) {
        WorldServer world = this.getWorld();
        newState.a((World)world, pos, oldState, false);
    }

    private void fireNeighborChanged(BlockPosition pos, WorldServer world, Block block, BlockPosition neighborPos) {
        world.a_(neighborPos).a((World)world, neighborPos, block, pos, false);
    }

    @Override
    public void updateNeighbors(BlockPosition pos, IBlockData oldState, IBlockData newState, int recursionLimit) {
        WorldServer world = this.getWorld();
        oldState.b((GeneratorAccess)world, pos, 2, recursionLimit);
        if (this.sideEffectSet.shouldApply(SideEffect.EVENTS)) {
            CraftWorld craftWorld = world.getWorld();
            BlockPhysicsEvent event = new BlockPhysicsEvent(craftWorld.getBlockAt(pos.u(), pos.v(), pos.w()), (BlockData)CraftBlockData.fromData((IBlockData)newState));
            world.getCraftServer().getPluginManager().callEvent((Event)event);
            if (event.isCancelled()) {
                return;
            }
        }
        newState.a((GeneratorAccess)world, pos, 2, recursionLimit);
        newState.b((GeneratorAccess)world, pos, 2, recursionLimit);
    }

    @Override
    public void onBlockStateChange(BlockPosition pos, IBlockData oldState, IBlockData newState) {
        this.getWorld().a(pos, oldState, newState);
    }
}

