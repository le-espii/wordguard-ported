/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 */
package com.sk89q.worldguard.protection.regions;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import com.sk89q.worldguard.util.MathUtils;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

public class ProtectedCuboidRegion
extends ProtectedRegion {
    public ProtectedCuboidRegion(String id, BlockVector3 pt1, BlockVector3 pt2) {
        this(id, false, pt1, pt2);
    }

    public ProtectedCuboidRegion(String id, boolean transientRegion, BlockVector3 pt1, BlockVector3 pt2) {
        super(id, transientRegion);
        this.setMinMaxPoints(pt1, pt2);
    }

    private void setMinMaxPoints(BlockVector3 position1, BlockVector3 position2) {
        Preconditions.checkNotNull((Object)position1);
        Preconditions.checkNotNull((Object)position2);
        ArrayList<BlockVector3> points = new ArrayList<BlockVector3>();
        points.add(position1);
        points.add(position2);
        this.setMinMaxPoints(points);
    }

    @Deprecated
    public void setMinimumPoint(BlockVector3 position) {
        WorldGuard.logger.warning("ProtectedCuboidRegion#setMinimumPoint call ignored. Mutating regions leads to undefined behavior.");
    }

    @Deprecated
    public void setMaximumPoint(BlockVector3 position) {
        WorldGuard.logger.warning("ProtectedCuboidRegion#setMaximumPoint call ignored. Mutating regions leads to undefined behavior.");
    }

    @Override
    public boolean isPhysicalArea() {
        return true;
    }

    @Override
    public List<BlockVector2> getPoints() {
        ArrayList<BlockVector2> pts = new ArrayList<BlockVector2>();
        int x1 = this.min.x();
        int x2 = this.max.x();
        int z1 = this.min.z();
        int z2 = this.max.z();
        pts.add(BlockVector2.at((int)x1, (int)z1));
        pts.add(BlockVector2.at((int)x2, (int)z1));
        pts.add(BlockVector2.at((int)x2, (int)z2));
        pts.add(BlockVector2.at((int)x1, (int)z2));
        return pts;
    }

    @Override
    public boolean contains(BlockVector3 pt) {
        double x = pt.x();
        double y = pt.y();
        double z = pt.z();
        return x >= (double)this.min.x() && x < (double)(this.max.x() + 1) && y >= (double)this.min.y() && y < (double)(this.max.y() + 1) && z >= (double)this.min.z() && z < (double)(this.max.z() + 1);
    }

    @Override
    public RegionType getType() {
        return RegionType.CUBOID;
    }

    @Override
    Area toArea() {
        int x = this.getMinimumPoint().x();
        int z = this.getMinimumPoint().z();
        int width = this.getMaximumPoint().x() - x + 1;
        int height = this.getMaximumPoint().z() - z + 1;
        return new Area(new Rectangle(x, z, width, height));
    }

    @Override
    protected boolean intersects(ProtectedRegion region, Area thisArea) {
        if (region instanceof ProtectedCuboidRegion) {
            return this.intersectsBoundingBox(region);
        }
        return super.intersects(region, thisArea);
    }

    @Override
    public int volume() {
        int xLength = this.max.x() - this.min.x() + 1;
        int yLength = this.max.y() - this.min.y() + 1;
        int zLength = this.max.z() - this.min.z() + 1;
        try {
            long v = MathUtils.checkedMultiply(xLength, yLength);
            v = MathUtils.checkedMultiply(v, zLength);
            if (v > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            return (int)v;
        }
        catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }
}

