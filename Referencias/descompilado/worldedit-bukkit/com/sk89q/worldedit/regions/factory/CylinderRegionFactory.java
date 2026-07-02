/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.factory;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.factory.RegionFactory;

public class CylinderRegionFactory
implements RegionFactory {
    private final double height;

    public CylinderRegionFactory(double height) {
        this.height = height;
    }

    @Override
    public Region createCenteredAt(BlockVector3 position, double size) {
        return new CylinderRegion(position, Vector2.at(size, size), position.y() - (int)(this.height / 2.0), position.y() + (int)(this.height / 2.0));
    }
}

