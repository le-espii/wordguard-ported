/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 */
package com.sk89q.worldguard.protection.regions;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GlobalProtectedRegion
extends ProtectedRegion {
    public GlobalProtectedRegion(String id) {
        this(id, false);
    }

    public GlobalProtectedRegion(String id, boolean transientRegion) {
        super(id, transientRegion);
        this.min = BlockVector3.ZERO;
        this.max = BlockVector3.ZERO;
    }

    @Override
    public boolean isPhysicalArea() {
        return false;
    }

    @Override
    public List<BlockVector2> getPoints() {
        ArrayList<BlockVector2> pts = new ArrayList<BlockVector2>();
        pts.add(BlockVector2.at((int)this.min.x(), (int)this.min.z()));
        return pts;
    }

    @Override
    public int volume() {
        return 0;
    }

    @Override
    public boolean contains(BlockVector3 pt) {
        return false;
    }

    @Override
    public RegionType getType() {
        return RegionType.GLOBAL;
    }

    @Override
    public List<ProtectedRegion> getIntersectingRegions(Collection<ProtectedRegion> regions) {
        return Collections.emptyList();
    }

    @Override
    Area toArea() {
        return null;
    }
}

