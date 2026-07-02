/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.factory;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.factory.RegionFactory;

public class SphereRegionFactory
implements RegionFactory {
    @Override
    public Region createCenteredAt(BlockVector3 position, double size) {
        return new EllipsoidRegion(position, Vector3.at(size, size, size));
    }
}

