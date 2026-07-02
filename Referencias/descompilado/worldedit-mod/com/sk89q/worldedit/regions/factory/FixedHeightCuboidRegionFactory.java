/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.factory;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.factory.RegionFactory;

public class FixedHeightCuboidRegionFactory
implements RegionFactory {
    private final int minY;
    private final int maxY;

    public FixedHeightCuboidRegionFactory(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public Region createCenteredAt(BlockVector3 position, double size) {
        CuboidRegion region = CuboidRegion.fromCenter(position, (int)size);
        region.setPos1(region.getPos1().withY(this.minY));
        region.setPos2(region.getPos2().withY(this.maxY));
        return region;
    }
}

