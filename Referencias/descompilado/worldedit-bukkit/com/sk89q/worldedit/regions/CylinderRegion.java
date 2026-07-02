/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.regions;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.geom.Polygons;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.iterator.FlatRegion3DIterator;
import com.sk89q.worldedit.regions.iterator.FlatRegionIterator;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

public class CylinderRegion
extends AbstractRegion
implements FlatRegion {
    private BlockVector2 center;
    private Vector2 radius;
    private int minY;
    private int maxY;
    private boolean hasY = false;
    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);

    public CylinderRegion() {
        this((World)null);
    }

    public CylinderRegion(World world) {
        this(world, BlockVector3.ZERO, Vector2.ZERO, 0, 0);
        this.hasY = false;
    }

    public CylinderRegion(World world, BlockVector3 center, Vector2 radius, int minY, int maxY) {
        super(world);
        this.setCenter(center.toBlockVector2());
        this.setRadius(radius);
        this.minY = minY;
        this.maxY = maxY;
        this.hasY = true;
    }

    public CylinderRegion(BlockVector3 center, Vector2 radius, int minY, int maxY) {
        super(null);
        this.setCenter(center.toBlockVector2());
        this.setRadius(radius);
        this.minY = minY;
        this.maxY = maxY;
        this.hasY = true;
    }

    public CylinderRegion(CylinderRegion region) {
        this(region.world, region.getCenter().toBlockPoint(), region.getRadius(), region.minY, region.maxY);
        this.hasY = region.hasY;
    }

    @Override
    public Vector3 getCenter() {
        return this.center.toVector3((this.maxY + this.minY) / 2);
    }

    public void setCenter(BlockVector2 center) {
        this.center = center;
    }

    public Vector2 getRadius() {
        return this.radius.subtract(0.5, 0.5);
    }

    public void setRadius(Vector2 radius) {
        this.radius = radius.add(0.5, 0.5);
    }

    public void extendRadius(Vector2 minRadius) {
        this.setRadius(minRadius.getMaximum(this.getRadius()));
    }

    public void setMinimumY(int y) {
        this.hasY = true;
        this.minY = y;
    }

    public void setMaximumY(int y) {
        this.hasY = true;
        this.maxY = y;
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.center.toVector2().subtract(this.getRadius()).toVector3(this.minY).toBlockPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.center.toVector2().add(this.getRadius()).toVector3(this.maxY).toBlockPoint();
    }

    @Override
    public int getMaximumY() {
        return this.maxY;
    }

    @Override
    public int getMinimumY() {
        return this.minY;
    }

    @Override
    public long getVolume() {
        return BigDecimal.valueOf(this.radius.x()).multiply(BigDecimal.valueOf(this.radius.z())).multiply(PI).multiply(BigDecimal.valueOf(this.getHeight())).setScale(0, RoundingMode.FLOOR).longValue();
    }

    @Override
    public int getWidth() {
        return (int)(2.0 * this.radius.x());
    }

    @Override
    public int getHeight() {
        return this.maxY - this.minY + 1;
    }

    @Override
    public int getLength() {
        return (int)(2.0 * this.radius.z());
    }

    private BlockVector2 calculateDiff2D(BlockVector3 ... changes) throws RegionOperationException {
        BlockVector2 diff = BlockVector2.ZERO;
        for (BlockVector3 change : changes) {
            diff = diff.add(change.toBlockVector2());
        }
        if ((diff.x() & 1) + (diff.z() & 1) != 0) {
            throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.cylinder.error.even-horizontal"));
        }
        return diff.divide(2).floor();
    }

    private BlockVector2 calculateChanges2D(BlockVector3 ... changes) {
        BlockVector2 total = BlockVector2.ZERO;
        for (BlockVector3 change : changes) {
            total = total.add(change.toBlockVector2().abs());
        }
        return total.divide(2).floor();
    }

    @Override
    public void expand(BlockVector3 ... changes) throws RegionOperationException {
        this.center = this.center.add(this.calculateDiff2D(changes));
        this.radius = this.radius.add(this.calculateChanges2D(changes).toVector2());
        for (BlockVector3 change : changes) {
            int changeY = change.y();
            if (changeY > 0) {
                this.maxY += changeY;
                continue;
            }
            this.minY += changeY;
        }
    }

    @Override
    public void contract(BlockVector3 ... changes) throws RegionOperationException {
        this.center = this.center.subtract(this.calculateDiff2D(changes));
        Vector2 newRadius = this.radius.subtract(this.calculateChanges2D(changes).toVector2());
        this.radius = Vector2.at(1.5, 1.5).getMaximum(newRadius);
        for (BlockVector3 change : changes) {
            int height = this.maxY - this.minY;
            int changeY = change.y();
            if (changeY > 0) {
                this.minY += Math.min(height, changeY);
                continue;
            }
            this.maxY += Math.max(-height, changeY);
        }
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        this.center = this.center.add(change.toBlockVector2());
        int changeY = change.y();
        this.maxY += changeY;
        this.minY += changeY;
    }

    @Override
    public boolean contains(BlockVector3 position) {
        int blockY = position.y();
        if (blockY < this.minY || blockY > this.maxY) {
            return false;
        }
        return position.toBlockVector2().subtract(this.center).toVector2().divide(this.radius).lengthSq() <= 1.0;
    }

    public boolean setY(int y) {
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
        return String.valueOf((Object)this.center) + " - " + String.valueOf((Object)this.radius) + "(" + this.minY + ", " + this.maxY + ")";
    }

    @Override
    public CylinderRegion clone() {
        return (CylinderRegion)super.clone();
    }

    @Override
    public List<BlockVector2> polygonize(int maxPoints) {
        return Polygons.polygonizeCylinder(this.center, this.radius, maxPoints);
    }

    public static CylinderRegion createRadius(Extent extent, BlockVector3 center, double radius) {
        Preconditions.checkNotNull((Object)extent);
        Preconditions.checkNotNull((Object)((Object)center));
        Vector2 radiusVec = Vector2.at(radius, radius);
        int minY = extent.getMinimumPoint().y();
        int maxY = extent.getMaximumPoint().y();
        return new CylinderRegion(center, radiusVec, minY, maxY);
    }
}

