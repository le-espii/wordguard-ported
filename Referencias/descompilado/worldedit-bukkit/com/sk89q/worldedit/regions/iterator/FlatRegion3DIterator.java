/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.regions.iterator;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.FlatRegion;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FlatRegion3DIterator
implements Iterator<BlockVector3> {
    private final Iterator<BlockVector2> flatIterator;
    private final int minY;
    private final int maxY;
    private BlockVector2 next2D;
    private int nextY;

    public FlatRegion3DIterator(FlatRegion region, Iterator<BlockVector2> flatIterator) {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull(flatIterator);
        this.flatIterator = flatIterator;
        this.minY = region.getMinimumY();
        this.maxY = region.getMaximumY();
        this.next2D = flatIterator.hasNext() ? flatIterator.next() : null;
        this.nextY = this.minY;
    }

    public FlatRegion3DIterator(FlatRegion region) {
        this(region, region.asFlatRegion().iterator());
    }

    @Override
    public boolean hasNext() {
        return this.next2D != null;
    }

    @Override
    public BlockVector3 next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        BlockVector3 current = BlockVector3.at(this.next2D.x(), this.nextY, this.next2D.z());
        if (this.nextY < this.maxY) {
            ++this.nextY;
        } else if (this.flatIterator.hasNext()) {
            this.next2D = this.flatIterator.next();
            this.nextY = this.minY;
        } else {
            this.next2D = null;
        }
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

