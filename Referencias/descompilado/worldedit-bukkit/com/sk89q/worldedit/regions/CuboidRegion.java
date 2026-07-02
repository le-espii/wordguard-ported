/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.regions;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionIntersection;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.world.World;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class CuboidRegion
extends AbstractRegion
implements FlatRegion {
    private BlockVector3 pos1;
    private BlockVector3 pos2;

    public CuboidRegion(BlockVector3 pos1, BlockVector3 pos2) {
        this(null, pos1, pos2);
    }

    public CuboidRegion(World world, BlockVector3 pos1, BlockVector3 pos2) {
        super(world);
        Preconditions.checkNotNull((Object)((Object)pos1));
        Preconditions.checkNotNull((Object)((Object)pos2));
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.recalculate();
    }

    public BlockVector3 getPos1() {
        return this.pos1;
    }

    public void setPos1(BlockVector3 pos1) {
        this.pos1 = pos1;
    }

    public BlockVector3 getPos2() {
        return this.pos2;
    }

    public void setPos2(BlockVector3 pos2) {
        this.pos2 = pos2;
    }

    private void recalculate() {
        this.pos1 = this.pos1.clampY(this.getWorldMinY(), this.getWorldMaxY());
        this.pos2 = this.pos2.clampY(this.getWorldMinY(), this.getWorldMaxY());
    }

    public Region getFaces() {
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        return new RegionIntersection(new CuboidRegion(this.pos1.withX(min.x()), this.pos2.withX(min.x())), new CuboidRegion(this.pos1.withX(max.x()), this.pos2.withX(max.x())), new CuboidRegion(this.pos1.withZ(min.z()), this.pos2.withZ(min.z())), new CuboidRegion(this.pos1.withZ(max.z()), this.pos2.withZ(max.z())), new CuboidRegion(this.pos1.withY(min.y()), this.pos2.withY(min.y())), new CuboidRegion(this.pos1.withY(max.y()), this.pos2.withY(max.y())));
    }

    public Region getWalls() {
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        return new RegionIntersection(new CuboidRegion(this.pos1.withX(min.x()), this.pos2.withX(min.x())), new CuboidRegion(this.pos1.withX(max.x()), this.pos2.withX(max.x())), new CuboidRegion(this.pos1.withZ(min.z()), this.pos2.withZ(min.z())), new CuboidRegion(this.pos1.withZ(max.z()), this.pos2.withZ(max.z())));
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.pos1.getMinimum(this.pos2);
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.pos1.getMaximum(this.pos2);
    }

    @Override
    public CuboidRegion getBoundingBox() {
        return this;
    }

    @Override
    public int getMinimumY() {
        return Math.min(this.pos1.y(), this.pos2.y());
    }

    @Override
    public int getMaximumY() {
        return Math.max(this.pos1.y(), this.pos2.y());
    }

    @Override
    public void expand(BlockVector3 ... changes) {
        Preconditions.checkNotNull((Object)changes);
        for (BlockVector3 change : changes) {
            if (change.x() > 0) {
                if (Math.max(this.pos1.x(), this.pos2.x()) == this.pos1.x()) {
                    this.pos1 = this.pos1.add(change.x(), 0, 0);
                } else {
                    this.pos2 = this.pos2.add(change.x(), 0, 0);
                }
            } else if (Math.min(this.pos1.x(), this.pos2.x()) == this.pos1.x()) {
                this.pos1 = this.pos1.add(change.x(), 0, 0);
            } else {
                this.pos2 = this.pos2.add(change.x(), 0, 0);
            }
            if (change.y() > 0) {
                if (Math.max(this.pos1.y(), this.pos2.y()) == this.pos1.y()) {
                    this.pos1 = this.pos1.add(0, change.y(), 0);
                } else {
                    this.pos2 = this.pos2.add(0, change.y(), 0);
                }
            } else if (Math.min(this.pos1.y(), this.pos2.y()) == this.pos1.y()) {
                this.pos1 = this.pos1.add(0, change.y(), 0);
            } else {
                this.pos2 = this.pos2.add(0, change.y(), 0);
            }
            if (change.z() > 0) {
                if (Math.max(this.pos1.z(), this.pos2.z()) == this.pos1.z()) {
                    this.pos1 = this.pos1.add(0, 0, change.z());
                    continue;
                }
                this.pos2 = this.pos2.add(0, 0, change.z());
                continue;
            }
            if (Math.min(this.pos1.z(), this.pos2.z()) == this.pos1.z()) {
                this.pos1 = this.pos1.add(0, 0, change.z());
                continue;
            }
            this.pos2 = this.pos2.add(0, 0, change.z());
        }
        this.recalculate();
    }

    @Override
    public void contract(BlockVector3 ... changes) {
        Preconditions.checkNotNull((Object)changes);
        for (BlockVector3 change : changes) {
            if (change.x() < 0) {
                if (Math.max(this.pos1.x(), this.pos2.x()) == this.pos1.x()) {
                    this.pos1 = this.pos1.add(change.x(), 0, 0);
                } else {
                    this.pos2 = this.pos2.add(change.x(), 0, 0);
                }
            } else if (Math.min(this.pos1.x(), this.pos2.x()) == this.pos1.x()) {
                this.pos1 = this.pos1.add(change.x(), 0, 0);
            } else {
                this.pos2 = this.pos2.add(change.x(), 0, 0);
            }
            if (change.y() < 0) {
                if (Math.max(this.pos1.y(), this.pos2.y()) == this.pos1.y()) {
                    this.pos1 = this.pos1.add(0, change.y(), 0);
                } else {
                    this.pos2 = this.pos2.add(0, change.y(), 0);
                }
            } else if (Math.min(this.pos1.y(), this.pos2.y()) == this.pos1.y()) {
                this.pos1 = this.pos1.add(0, change.y(), 0);
            } else {
                this.pos2 = this.pos2.add(0, change.y(), 0);
            }
            if (change.z() < 0) {
                if (Math.max(this.pos1.z(), this.pos2.z()) == this.pos1.z()) {
                    this.pos1 = this.pos1.add(0, 0, change.z());
                    continue;
                }
                this.pos2 = this.pos2.add(0, 0, change.z());
                continue;
            }
            if (Math.min(this.pos1.z(), this.pos2.z()) == this.pos1.z()) {
                this.pos1 = this.pos1.add(0, 0, change.z());
                continue;
            }
            this.pos2 = this.pos2.add(0, 0, change.z());
        }
        this.recalculate();
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        this.pos1 = this.pos1.add(change);
        this.pos2 = this.pos2.add(change);
        this.recalculate();
    }

    @Override
    public Set<BlockVector2> getChunks() {
        HashSet<BlockVector2> chunks = new HashSet<BlockVector2>();
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        for (int x = min.x() >> 4; x <= max.x() >> 4; ++x) {
            for (int z = min.z() >> 4; z <= max.z() >> 4; ++z) {
                chunks.add(BlockVector2.at(x, z));
            }
        }
        return chunks;
    }

    @Override
    public Set<BlockVector3> getChunkCubes() {
        HashSet<BlockVector3> chunks = new HashSet<BlockVector3>();
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        for (int x = min.x() >> 4; x <= max.x() >> 4; ++x) {
            for (int z = min.z() >> 4; z <= max.z() >> 4; ++z) {
                for (int y = min.y() >> 4; y <= max.y() >> 4; ++y) {
                    chunks.add(BlockVector3.at(x, y, z));
                }
            }
        }
        return chunks;
    }

    @Override
    public boolean contains(BlockVector3 position) {
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        return position.containedWithin(min, max);
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return new Iterator<BlockVector3>(){
            private final BlockVector3 min;
            private final BlockVector3 max;
            private int nextX;
            private int nextY;
            private int nextZ;
            {
                this.min = CuboidRegion.this.getMinimumPoint();
                this.max = CuboidRegion.this.getMaximumPoint();
                this.nextX = this.min.x();
                this.nextY = this.min.y();
                this.nextZ = this.min.z();
            }

            @Override
            public boolean hasNext() {
                return this.nextX != Integer.MIN_VALUE;
            }

            @Override
            public BlockVector3 next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                BlockVector3 answer = BlockVector3.at(this.nextX, this.nextY, this.nextZ);
                if (++this.nextX > this.max.x()) {
                    this.nextX = this.min.x();
                    if (++this.nextZ > this.max.z()) {
                        this.nextZ = this.min.z();
                        if (++this.nextY > this.max.y()) {
                            this.nextX = Integer.MIN_VALUE;
                        }
                    }
                }
                return answer;
            }
        };
    }

    @Override
    public Iterable<BlockVector2> asFlatRegion() {
        return () -> new Iterator<BlockVector2>(){
            private final BlockVector3 min;
            private final BlockVector3 max;
            private int nextX;
            private int nextZ;
            {
                this.min = CuboidRegion.this.getMinimumPoint();
                this.max = CuboidRegion.this.getMaximumPoint();
                this.nextX = this.min.x();
                this.nextZ = this.min.z();
            }

            @Override
            public boolean hasNext() {
                return this.nextX != Integer.MIN_VALUE;
            }

            @Override
            public BlockVector2 next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                BlockVector2 answer = BlockVector2.at(this.nextX, this.nextZ);
                if (++this.nextX > this.max.x()) {
                    this.nextX = this.min.x();
                    if (++this.nextZ > this.max.z()) {
                        this.nextX = Integer.MIN_VALUE;
                    }
                }
                return answer;
            }
        };
    }

    public String toString() {
        return String.valueOf((Object)this.getMinimumPoint()) + " - " + String.valueOf((Object)this.getMaximumPoint());
    }

    @Override
    public CuboidRegion clone() {
        return (CuboidRegion)super.clone();
    }

    public static CuboidRegion makeCuboid(Region region) {
        Preconditions.checkNotNull((Object)region);
        return new CuboidRegion(region.getMinimumPoint(), region.getMaximumPoint());
    }

    public static CuboidRegion fromCenter(BlockVector3 origin, int apothem) {
        Preconditions.checkNotNull((Object)((Object)origin));
        Preconditions.checkArgument((apothem >= 0 ? 1 : 0) != 0, (Object)"apothem => 0 required");
        BlockVector3 size = BlockVector3.ONE.multiply(apothem);
        return new CuboidRegion(origin.subtract(size), origin.add(size));
    }
}

