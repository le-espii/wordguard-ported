/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.iterator.FlatRegion3DIterator;
import com.sk89q.worldedit.regions.iterator.FlatRegionIterator;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Polygonal2DRegion
extends AbstractRegion
implements FlatRegion {
    private List<BlockVector2> points;
    private BlockVector2 min;
    private BlockVector2 max;
    private int minY;
    private int maxY;
    private boolean hasY = false;

    public Polygonal2DRegion() {
        this((World)null);
    }

    public Polygonal2DRegion(World world) {
        this(world, Collections.emptyList(), 0, 0);
        this.hasY = false;
    }

    public Polygonal2DRegion(World world, List<BlockVector2> points, int minY, int maxY) {
        super(world);
        this.points = new ArrayList<BlockVector2>(points);
        this.minY = minY;
        this.maxY = maxY;
        this.hasY = true;
        this.recalculate();
    }

    public Polygonal2DRegion(Polygonal2DRegion region) {
        this(region.world, region.points, region.minY, region.maxY);
        this.hasY = region.hasY;
    }

    public List<BlockVector2> getPoints() {
        return Collections.unmodifiableList(this.points);
    }

    protected void recalculate() {
        if (this.points.isEmpty()) {
            this.min = BlockVector2.ZERO;
            this.minY = 0;
            this.max = BlockVector2.ZERO;
            this.maxY = 0;
            return;
        }
        int minX = this.points.get(0).x();
        int minZ = this.points.get(0).z();
        int maxX = this.points.get(0).x();
        int maxZ = this.points.get(0).z();
        for (BlockVector2 v : this.points) {
            int x = v.x();
            int z = v.z();
            if (x < minX) {
                minX = x;
            }
            if (z < minZ) {
                minZ = z;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (z <= maxZ) continue;
            maxZ = z;
        }
        int oldMinY = this.minY;
        int oldMaxY = this.maxY;
        this.minY = Math.min(oldMinY, oldMaxY);
        this.maxY = Math.max(oldMinY, oldMaxY);
        this.minY = Math.min(Math.max(this.getWorldMinY(), this.minY), this.getWorldMaxY());
        this.maxY = Math.min(Math.max(this.getWorldMinY(), this.maxY), this.getWorldMaxY());
        this.min = BlockVector2.at(minX, minZ);
        this.max = BlockVector2.at(maxX, maxZ);
    }

    public void addPoint(BlockVector2 position) {
        this.points.add(position);
        this.recalculate();
    }

    public void addPoint(BlockVector3 position) {
        this.points.add(BlockVector2.at(position.x(), position.z()));
        this.recalculate();
    }

    @Override
    public int getMinimumY() {
        return this.minY;
    }

    public void setMinimumY(int y) {
        this.hasY = true;
        this.minY = y;
        this.recalculate();
    }

    @Override
    public int getMaximumY() {
        return this.maxY;
    }

    public void setMaximumY(int y) {
        this.hasY = true;
        this.maxY = y;
        this.recalculate();
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.min.toBlockVector3(this.minY);
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.max.toBlockVector3(this.maxY);
    }

    @Override
    public long getVolume() {
        long area = 0L;
        int j = this.points.size() - 1;
        int i = 0;
        while (i < this.points.size()) {
            long x = this.points.get(j).x() + this.points.get(i).x();
            long z = this.points.get(j).z() - this.points.get(i).z();
            area += x * z;
            j = i++;
        }
        return BigDecimal.valueOf(area).multiply(BigDecimal.valueOf(0.5)).abs().setScale(0, RoundingMode.FLOOR).longValue() * (long)(this.maxY - this.minY + 1);
    }

    @Override
    public int getWidth() {
        return this.max.x() - this.min.x() + 1;
    }

    @Override
    public int getHeight() {
        return this.maxY - this.minY + 1;
    }

    @Override
    public int getLength() {
        return this.max.z() - this.min.z() + 1;
    }

    @Override
    public void expand(BlockVector3 ... changes) throws RegionOperationException {
        for (BlockVector3 change : changes) {
            if (change.x() != 0 || change.z() != 0) {
                throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.polygon2d.error.expand-only-vertical"));
            }
            int changeY = change.y();
            if (changeY > 0) {
                this.maxY += changeY;
                continue;
            }
            this.minY += changeY;
        }
        this.recalculate();
    }

    @Override
    public void contract(BlockVector3 ... changes) throws RegionOperationException {
        for (BlockVector3 change : changes) {
            if (change.x() != 0 || change.z() != 0) {
                throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.polygon2d.error.contract-only-vertical"));
            }
            int changeY = change.y();
            if (changeY > 0) {
                this.minY += changeY;
                continue;
            }
            this.maxY += changeY;
        }
        this.recalculate();
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        for (int i = 0; i < this.points.size(); ++i) {
            BlockVector2 point = this.points.get(i);
            this.points.set(i, BlockVector2.at(point.x() + change.x(), point.z() + change.z()));
        }
        this.minY += change.y();
        this.maxY += change.y();
        this.recalculate();
    }

    @Override
    public boolean contains(BlockVector3 position) {
        return Polygonal2DRegion.contains(this.points, this.minY, this.maxY, position);
    }

    public static boolean contains(List<BlockVector2> points, int minY, int maxY, BlockVector3 pt) {
        if (points.size() < 3) {
            return false;
        }
        int targetX = pt.x();
        int targetY = pt.y();
        int targetZ = pt.z();
        if (targetY < minY || targetY > maxY) {
            return false;
        }
        boolean inside = false;
        int npoints = points.size();
        int xOld = points.get(npoints - 1).x();
        int zOld = points.get(npoints - 1).z();
        for (int i = 0; i < npoints; ++i) {
            int z2;
            int z1;
            int x2;
            int x1;
            int xNew = points.get(i).x();
            int zNew = points.get(i).z();
            if (xNew == targetX && zNew == targetZ) {
                return true;
            }
            if (xNew > xOld) {
                x1 = xOld;
                x2 = xNew;
                z1 = zOld;
                z2 = zNew;
            } else {
                x1 = xNew;
                x2 = xOld;
                z1 = zNew;
                z2 = zOld;
            }
            if (x1 <= targetX && targetX <= x2) {
                long crossproduct = ((long)targetZ - (long)z1) * (long)(x2 - x1) - ((long)z2 - (long)z1) * (long)(targetX - x1);
                if (crossproduct == 0L) {
                    if (z1 <= targetZ == targetZ <= z2) {
                        return true;
                    }
                } else if (crossproduct < 0L && x1 != targetX) {
                    inside = !inside;
                }
            }
            xOld = xNew;
            zOld = zNew;
        }
        return inside;
    }

    public int size() {
        return this.points.size();
    }

    public boolean expandY(int y) {
        if (!this.hasY) {
            this.minY = y;
            this.maxY = y;
            this.hasY = true;
            return true;
        }
        if (y < this.minY) {
            this.minY = y;
            return true;
        }
        if (y > this.maxY) {
            this.maxY = y;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return new FlatRegion3DIterator(this);
    }

    @Override
    public Iterable<BlockVector2> asFlatRegion() {
        return () -> new FlatRegionIterator(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<BlockVector2> pts = this.getPoints();
        Iterator<BlockVector2> it = pts.iterator();
        while (it.hasNext()) {
            BlockVector2 current = it.next();
            sb.append("(").append(current.x()).append(", ").append(current.z()).append(")");
            if (!it.hasNext()) continue;
            sb.append(" - ");
        }
        sb.append(" * (").append(this.minY).append(" - ").append(this.maxY).append(")");
        return sb.toString();
    }

    @Override
    public Polygonal2DRegion clone() {
        Polygonal2DRegion clone = (Polygonal2DRegion)super.clone();
        clone.points = new ArrayList<BlockVector2>(this.points);
        return clone;
    }

    @Override
    public List<BlockVector2> polygonize(int maxPoints) {
        if (maxPoints >= 0 && maxPoints < this.points.size()) {
            throw new IllegalArgumentException("Cannot polygonize a this Polygonal2DRegion into the amount of points given.");
        }
        return this.points;
    }
}

