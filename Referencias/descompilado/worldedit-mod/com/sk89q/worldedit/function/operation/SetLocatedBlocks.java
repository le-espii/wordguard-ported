/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.operation;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.util.LocatedBlock;

public class SetLocatedBlocks
implements Operation {
    private final Extent extent;
    private final Iterable<LocatedBlock> blocks;

    public SetLocatedBlocks(Extent extent, Iterable<LocatedBlock> blocks) {
        this.extent = (Extent)Preconditions.checkNotNull((Object)extent);
        this.blocks = (Iterable)Preconditions.checkNotNull(blocks);
    }

    @Override
    public Operation resume(RunContext run) throws WorldEditException {
        for (LocatedBlock block : this.blocks) {
            this.extent.setBlock(block.location(), block.block());
        }
        return null;
    }

    @Override
    public void cancel() {
    }
}

