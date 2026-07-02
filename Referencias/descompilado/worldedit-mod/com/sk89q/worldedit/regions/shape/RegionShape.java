/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.shape;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.shape.ArbitraryShape;
import com.sk89q.worldedit.world.block.BaseBlock;

public class RegionShape
extends ArbitraryShape {
    public RegionShape(Region extent) {
        super(extent);
    }

    @Override
    protected BaseBlock getMaterial(int x, int y, int z, BaseBlock defaultMaterial) {
        if (!this.extent.contains(BlockVector3.at(x, y, z))) {
            return null;
        }
        return defaultMaterial;
    }
}

