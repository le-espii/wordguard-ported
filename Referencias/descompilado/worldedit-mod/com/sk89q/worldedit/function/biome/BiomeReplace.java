/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.biome;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.FlatRegionFunction;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.pattern.BiomePattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;

public class BiomeReplace
implements FlatRegionFunction,
RegionFunction {
    private final Extent extent;
    private final BiomePattern biome;

    public BiomeReplace(Extent extent, BiomeType biome) {
        this(extent, (BiomePattern)biome);
    }

    public BiomeReplace(Extent extent, BiomePattern pattern) {
        Preconditions.checkNotNull((Object)extent);
        Preconditions.checkNotNull((Object)pattern);
        this.extent = extent;
        this.biome = pattern;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        return this.extent.setBiome(position, this.biome.applyBiome(position));
    }

    @Override
    @Deprecated
    public boolean apply(BlockVector2 position) throws WorldEditException {
        boolean success = false;
        for (int y = this.extent.getMinimumPoint().y(); y <= this.extent.getMaximumPoint().y(); ++y) {
            success |= this.apply(position.toBlockVector3(y));
        }
        return success;
    }
}

