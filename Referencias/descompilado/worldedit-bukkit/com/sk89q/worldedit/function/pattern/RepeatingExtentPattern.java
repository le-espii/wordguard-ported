/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.pattern;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractExtentPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;

public class RepeatingExtentPattern
extends AbstractExtentPattern {
    private final BlockVector3 size;
    private BlockVector3 origin;
    private BlockVector3 offset;

    public RepeatingExtentPattern(Extent extent, BlockVector3 origin, BlockVector3 offset) {
        super(extent);
        this.setOrigin(origin);
        this.setOffset(offset);
        this.size = extent.getMaximumPoint().subtract(extent.getMinimumPoint()).add(1, 1, 1);
    }

    public BlockVector3 getOffset() {
        return this.offset;
    }

    public void setOffset(BlockVector3 offset) {
        Preconditions.checkNotNull((Object)((Object)offset));
        this.offset = offset;
    }

    public BlockVector3 getOrigin() {
        return this.origin;
    }

    public void setOrigin(BlockVector3 origin) {
        Preconditions.checkNotNull((Object)((Object)origin));
        this.origin = origin;
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        BlockVector3 base = position.add(this.offset);
        int x = Math.floorMod(base.x(), this.size.x());
        int y = Math.floorMod(base.y(), this.size.y());
        int z = Math.floorMod(base.z(), this.size.z());
        return this.getExtent().getFullBlock(BlockVector3.at(x, y, z).add(this.origin));
    }
}

