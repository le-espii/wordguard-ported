/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.cache;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class LastAccessExtentCache
extends AbstractDelegateExtent {
    private CachedBlock<BlockState> lastBlock;
    private CachedBlock<BaseBlock> lastFullBlock;

    public LastAccessExtentCache(Extent extent) {
        super(extent);
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        CachedBlock<BlockState> lastBlock = this.lastBlock;
        if (lastBlock != null && lastBlock.position.equals((Object)position)) {
            return (BlockState)lastBlock.block;
        }
        if (this.lastFullBlock != null && this.lastFullBlock.position.equals((Object)position)) {
            return this.lastFullBlock.block().toImmutableState();
        }
        BlockState block = super.getBlock(position);
        this.lastBlock = new CachedBlock<BlockState>(position, block);
        return block;
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        CachedBlock<BaseBlock> lastFullBlock = this.lastFullBlock;
        if (lastFullBlock != null && lastFullBlock.position.equals((Object)position)) {
            return (BaseBlock)lastFullBlock.block;
        }
        BaseBlock block = super.getFullBlock(position);
        this.lastFullBlock = new CachedBlock<BaseBlock>(position, block);
        return block;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        if (super.setBlock(location, block)) {
            if (this.lastFullBlock != null && this.lastFullBlock.position.equals((Object)location)) {
                this.lastFullBlock = new CachedBlock<BaseBlock>(location, block.toBaseBlock());
            }
            if (this.lastBlock != null && this.lastBlock.position.equals((Object)location)) {
                this.lastBlock = new CachedBlock<BlockState>(location, block.toImmutableState());
            }
            return true;
        }
        return false;
    }

    private record CachedBlock<B extends BlockStateHolder<B>>(BlockVector3 position, B block) {
    }
}

