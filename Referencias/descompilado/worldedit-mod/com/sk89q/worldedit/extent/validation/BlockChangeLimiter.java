/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent.validation;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class BlockChangeLimiter
extends AbstractDelegateExtent {
    private int limit;
    private int count = 0;

    public BlockChangeLimiter(Extent extent, int limit) {
        super(extent);
        this.setLimit(limit);
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        Preconditions.checkArgument((limit >= -1 ? 1 : 0) != 0, (Object)"limit >= -1 required");
        this.limit = limit;
    }

    public int getCount() {
        return this.count;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        if (this.limit >= 0) {
            if (this.count >= this.limit) {
                throw new MaxChangedBlocksException(this.limit);
            }
            ++this.count;
        }
        return super.setBlock(location, block);
    }
}

