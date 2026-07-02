/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.iterator.RegionIterator;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AbstractRegion
implements Region {
    protected World world;

    public AbstractRegion(World world) {
        this.world = world;
    }

    @Override
    public Vector3 getCenter() {
        return this.getMinimumPoint().add(this.getMaximumPoint()).toVector3().divide(2.0);
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return new RegionIterator(this);
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        this.expand(change);
        this.contract(change);
    }

    @Override
    public AbstractRegion clone() {
        try {
            return (AbstractRegion)super.clone();
        }
        catch (CloneNotSupportedException exc) {
            return null;
        }
    }

    @Override
    public List<BlockVector2> polygonize(int maxPoints) {
        if (maxPoints >= 0 && maxPoints < 4) {
            throw new IllegalArgumentException("Cannot polygonize an AbstractRegion with no overridden polygonize method into less than 4 points.");
        }
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        ArrayList<BlockVector2> points = new ArrayList<BlockVector2>(4);
        points.add(BlockVector2.at(min.x(), min.z()));
        points.add(BlockVector2.at(min.x(), max.z()));
        points.add(BlockVector2.at(max.x(), max.z()));
        points.add(BlockVector2.at(max.x(), min.z()));
        return points;
    }

    @Override
    public long getVolume() {
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        return ((long)(max.x() - min.x()) + 1L) * ((long)(max.y() - min.y()) + 1L) * ((long)(max.z() - min.z()) + 1L);
    }

    @Override
    public int getWidth() {
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        return max.x() - min.x() + 1;
    }

    @Override
    public int getHeight() {
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        return max.y() - min.y() + 1;
    }

    @Override
    public int getLength() {
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        return max.z() - min.z() + 1;
    }

    @Override
    public Set<BlockVector2> getChunks() {
        HashSet<BlockVector2> chunks = new HashSet<BlockVector2>();
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        int minY = min.y();
        for (int x = min.x(); x <= max.x(); ++x) {
            for (int z = min.z(); z <= max.z(); ++z) {
                if (!this.contains(BlockVector3.at(x, minY, z))) continue;
                chunks.add(BlockVector2.at(x >> 4, z >> 4));
            }
        }
        return chunks;
    }

    @Override
    public Set<BlockVector3> getChunkCubes() {
        HashSet<BlockVector3> chunks = new HashSet<BlockVector3>();
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        for (int x = min.x(); x <= max.x(); ++x) {
            for (int y = min.y(); y <= max.y(); ++y) {
                for (int z = min.z(); z <= max.z(); ++z) {
                    if (!this.contains(BlockVector3.at(x, y, z))) continue;
                    chunks.add(BlockVector3.at(x >> 4, y >> 4, z >> 4));
                }
            }
        }
        return chunks;
    }

    protected final int getWorldMinY() {
        return this.world == null ? Integer.MIN_VALUE : this.world.getMinY();
    }

    protected final int getWorldMaxY() {
        return this.world == null ? Integer.MAX_VALUE : this.world.getMaxY();
    }
}

