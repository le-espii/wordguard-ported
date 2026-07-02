/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 */
package com.sk89q.worldguard.protection.regions;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

public class ProtectedPolygonalRegion
extends ProtectedRegion {
    private final ImmutableList<BlockVector2> points;
    private final int minY;
    private final int maxY;

    public ProtectedPolygonalRegion(String id, List<BlockVector2> points, int minY, int maxY) {
        this(id, false, points, minY, maxY);
    }

    public ProtectedPolygonalRegion(String id, boolean transientRegion, List<BlockVector2> points, int minY, int maxY) {
        super(id, transientRegion);
        ImmutableList immutablePoints = ImmutableList.copyOf(points);
        this.setMinMaxPoints((List<BlockVector2>)immutablePoints, minY, maxY);
        this.points = immutablePoints;
        this.minY = this.min.y();
        this.maxY = this.max.y();
    }

    private void setMinMaxPoints(List<BlockVector2> points2D, int minY, int maxY) {
        Preconditions.checkNotNull(points2D);
        ArrayList<BlockVector3> points = new ArrayList<BlockVector3>();
        int y = minY;
        for (BlockVector2 point2D : points2D) {
            points.add(BlockVector3.at((int)point2D.x(), (int)y, (int)point2D.z()));
            y = maxY;
        }
        this.setMinMaxPoints(points);
    }

    @Override
    public boolean isPhysicalArea() {
        return true;
    }

    @Override
    public List<BlockVector2> getPoints() {
        return this.points;
    }

    @Override
    public boolean contains(BlockVector3 position) {
        Preconditions.checkNotNull((Object)position);
        int targetX = position.x();
        int targetY = position.y();
        int targetZ = position.z();
        if (targetY < this.minY || targetY > this.maxY) {
            return false;
        }
        if (targetX < this.min.x() || targetX > this.max.x() || targetZ < this.min.z() || targetZ > this.max.z()) {
            return false;
        }
        boolean inside = false;
        int npoints = this.points.size();
        int xOld = ((BlockVector2)this.points.get(npoints - 1)).x();
        int zOld = ((BlockVector2)this.points.get(npoints - 1)).z();
        for (int i = 0; i < npoints; ++i) {
            int z2;
            int z1;
            int x2;
            int x1;
            int xNew = ((BlockVector2)this.points.get(i)).x();
            int zNew = ((BlockVector2)this.points.get(i)).z();
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

    @Override
    public RegionType getType() {
        return RegionType.POLYGON;
    }

    @Override
    Area toArea() {
        List<BlockVector2> points = this.getPoints();
        int numPoints = points.size();
        int[] xCoords = new int[numPoints];
        int[] yCoords = new int[numPoints];
        int i = 0;
        for (BlockVector2 point : points) {
            xCoords[i] = point.x();
            yCoords[i] = point.z();
            ++i;
        }
        Polygon polygon = new Polygon(xCoords, yCoords, numPoints);
        return new Area(polygon);
    }

    @Override
    public int volume() {
        return 0;
    }
}

