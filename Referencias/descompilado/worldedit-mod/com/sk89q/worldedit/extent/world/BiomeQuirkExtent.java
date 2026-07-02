/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.world;

import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;

public class BiomeQuirkExtent
extends AbstractDelegateExtent {
    public BiomeQuirkExtent(Extent extent) {
        super(extent);
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        boolean success = false;
        if (!this.fullySupports3DBiomes()) {
            success = super.setBiome(position.withY(0), biome);
        }
        return super.setBiome(position, biome) || success;
    }
}

