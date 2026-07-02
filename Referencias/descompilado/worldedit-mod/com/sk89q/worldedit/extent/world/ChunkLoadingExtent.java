/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent.world;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class ChunkLoadingExtent
extends AbstractDelegateExtent {
    private final World world;
    private final boolean enabled;

    public ChunkLoadingExtent(Extent extent, World world, boolean enabled) {
        super(extent);
        Preconditions.checkNotNull((Object)world);
        this.enabled = enabled;
        this.world = world;
    }

    public ChunkLoadingExtent(Extent extent, World world) {
        this(extent, world, true);
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        if (this.enabled) {
            this.world.checkLoadedChunk(location);
        }
        return super.setBlock(location, block);
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        if (this.enabled) {
            this.world.checkLoadedChunk(position);
        }
        return super.setBiome(position, biome);
    }
}

