/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.reorder;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractBufferingExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.internal.util.RegionOptimizedVectorSorter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.ArrayList;

public class ChunkBatchingExtent
extends AbstractBufferingExtent {
    private final BlockMap<BaseBlock> blockMap = BlockMap.createForBaseBlock();
    private boolean enabled;

    public ChunkBatchingExtent(Extent extent) {
        this(extent, true);
    }

    public ChunkBatchingExtent(Extent extent, boolean enabled) {
        super(extent);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean commitRequired() {
        return this.enabled;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        if (!this.enabled) {
            return this.setDelegateBlock(location, block);
        }
        this.blockMap.put(location, block.toBaseBlock());
        return true;
    }

    @Override
    protected BaseBlock getBufferedFullBlock(BlockVector3 position) {
        if (!this.enabled) {
            return null;
        }
        return this.blockMap.get((Object)position);
    }

    @Override
    protected Operation commitBefore() {
        if (!this.commitRequired()) {
            return null;
        }
        return new Operation(){

            @Override
            public Operation resume(RunContext run) throws WorldEditException {
                ArrayList<BlockVector3> blockVectors = new ArrayList<BlockVector3>(ChunkBatchingExtent.this.blockMap.keySet());
                RegionOptimizedVectorSorter.sort(blockVectors);
                for (BlockVector3 position : blockVectors) {
                    BaseBlock block = ChunkBatchingExtent.this.blockMap.get((Object)position);
                    ChunkBatchingExtent.this.getExtent().setBlock(position, block);
                }
                ChunkBatchingExtent.this.blockMap.clear();
                return null;
            }

            @Override
            public void cancel() {
            }
        };
    }
}

