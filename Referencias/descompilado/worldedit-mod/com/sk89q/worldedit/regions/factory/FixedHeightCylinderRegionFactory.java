/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.factory;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.factory.RegionFactory;

public class FixedHeightCylinderRegionFactory
implements RegionFactory {
    private final int minY;
    private final int maxY;

    public FixedHeightCylinderRegionFactory(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public Region createCenteredAt(BlockVector3 position, double size) {
        return new CylinderRegion(position, Vector2.at(size, size), this.minY, this.maxY);
    }
}

