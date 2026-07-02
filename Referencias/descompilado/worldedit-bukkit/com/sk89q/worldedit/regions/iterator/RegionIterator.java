/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.regions.iterator;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RegionIterator
implements Iterator<BlockVector3> {
    private final Region region;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final BlockVector3 min;
    private int nextX;
    private int nextY;
    private int nextZ;

    public RegionIterator(Region region) {
        Preconditions.checkNotNull((Object)region);
        this.region = region;
        BlockVector3 max = region.getMaximumPoint();
        this.maxX = max.x();
        this.maxY = max.y();
        this.maxZ = max.z();
        this.min = region.getMinimumPoint();
        this.nextX = this.min.x();
        this.nextY = this.min.y();
        this.nextZ = this.min.z();
        this.forward();
    }

    @Override
    public boolean hasNext() {
        return this.nextX != Integer.MIN_VALUE;
    }

    private void forward() {
        while (this.hasNext() && !this.region.contains(BlockVector3.at(this.nextX, this.nextY, this.nextZ))) {
            this.forwardOne();
        }
    }

    @Override
    public BlockVector3 next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        BlockVector3 answer = BlockVector3.at(this.nextX, this.nextY, this.nextZ);
        this.forwardOne();
        this.forward();
        return answer;
    }

    private void forwardOne() {
        if (++this.nextX <= this.maxX) {
            return;
        }
        this.nextX = this.min.x();
        if (++this.nextY <= this.maxY) {
            return;
        }
        this.nextY = this.min.y();
        if (++this.nextZ <= this.maxZ) {
            return;
        }
        this.nextX = Integer.MIN_VALUE;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

