/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.regions.CuboidRegion
 *  com.sk89q.worldedit.regions.Polygonal2DRegion
 *  com.sk89q.worldedit.regions.Region
 *  com.sk89q.worldedit.regions.RegionSelector
 *  com.sk89q.worldedit.regions.selector.CuboidRegionSelector
 *  com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector
 */
package com.sk89q.worldguard.protection.util;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class WorldEditRegionConverter {
    private WorldEditRegionConverter() {
    }

    public static Region convertToRegion(ProtectedRegion region) {
        if (region instanceof ProtectedCuboidRegion) {
            return new CuboidRegion(null, region.getMinimumPoint(), region.getMaximumPoint());
        }
        if (region instanceof ProtectedPolygonalRegion) {
            return new Polygonal2DRegion(null, region.getPoints(), region.getMinimumPoint().y(), region.getMaximumPoint().y());
        }
        return null;
    }

    public static RegionSelector convertToSelector(ProtectedRegion region) {
        if (region instanceof ProtectedCuboidRegion) {
            return new CuboidRegionSelector(null, region.getMinimumPoint(), region.getMaximumPoint());
        }
        if (region instanceof ProtectedPolygonalRegion) {
            return new Polygonal2DRegionSelector(null, region.getPoints(), region.getMinimumPoint().y(), region.getMaximumPoint().y());
        }
        return null;
    }
}

