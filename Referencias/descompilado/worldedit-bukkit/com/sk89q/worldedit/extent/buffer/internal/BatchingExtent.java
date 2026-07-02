/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 */
package com.sk89q.worldedit.extent.buffer.internal;

import com.google.common.base.Throwables;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractBufferingExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class BatchingExtent
extends AbstractBufferingExtent {
    private final BlockMap<BaseBlock> blockMap = BlockMap.createForBaseBlock();
    private boolean enabled;

    public BatchingExtent(Extent extent) {
        this(extent, true);
    }

    public BatchingExtent(Extent extent, boolean enabled) {
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
                try {
                    BatchingExtent.this.blockMap.forEach((position, block) -> {
                        try {
                            BatchingExtent.this.getExtent().setBlock((BlockVector3)((Object)position), block);
                        }
                        catch (WorldEditException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                catch (RuntimeException e) {
                    Throwables.throwIfInstanceOf((Throwable)e.getCause(), WorldEditException.class);
                    throw e;
                }
                BatchingExtent.this.blockMap.clear();
                return null;
            }

            @Override
            public void cancel() {
            }
        };
    }
}

