/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Registry
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.UpgradeData
 *  net.minecraft.world.level.levelgen.blending.BlendingData
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.Slice
 */
package com.sk89q.worldedit.neoforge.mixin;

import com.sk89q.worldedit.neoforge.internal.ExtendedChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value={LevelChunk.class})
public abstract class MixinLevelChunkSetBlockHook
extends ChunkAccess
implements ExtendedChunk {
    private boolean shouldUpdate = true;

    public MixinLevelChunkSetBlockHook(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> registry, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @javax.annotation.Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved, boolean update) {
        this.shouldUpdate = update;
        try {
            BlockState blockState = this.setBlockState(pos, state, moved);
            return blockState;
        }
        finally {
            this.shouldUpdate = true;
        }
    }

    @Redirect(method={"setBlockState"}, slice=@Slice(from=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z")), at=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/state/BlockState;onPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"))
    public void setBlockStateHook(BlockState target, Level world, BlockPos pos, BlockState old, boolean move) {
        MinecraftServer server = world.getServer();
        boolean localShouldUpdate = server == null || Thread.currentThread() != server.getRunningThread() ? true : this.shouldUpdate;
        if (localShouldUpdate) {
            target.onPlace(world, pos, old, move);
        }
    }
}

