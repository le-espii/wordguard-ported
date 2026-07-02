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
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.world.biome.BiomeType;

public class ExtentBiomeCopy
implements FlatRegionFunction,
RegionFunction {
    private final Extent source;
    private final Extent destination;
    private final BlockVector3 from;
    private final BlockVector3 to;
    private final Transform transform;

    @Deprecated
    public ExtentBiomeCopy(Extent source, BlockVector2 from, Extent destination, BlockVector2 to, Transform transform) {
        Preconditions.checkNotNull((Object)source);
        Preconditions.checkNotNull((Object)((Object)from));
        Preconditions.checkNotNull((Object)destination);
        Preconditions.checkNotNull((Object)((Object)to));
        Preconditions.checkNotNull((Object)transform);
        this.source = source;
        this.from = from.toBlockVector3();
        this.destination = destination;
        this.to = to.toBlockVector3();
        this.transform = transform;
    }

    public ExtentBiomeCopy(Extent source, BlockVector3 from, Extent destination, BlockVector3 to, Transform transform) {
        Preconditions.checkNotNull((Object)source);
        Preconditions.checkNotNull((Object)((Object)from));
        Preconditions.checkNotNull((Object)destination);
        Preconditions.checkNotNull((Object)((Object)to));
        Preconditions.checkNotNull((Object)transform);
        this.source = source;
        this.from = from;
        this.destination = destination;
        this.to = to;
        this.transform = transform;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        BiomeType biome = this.source.getBiome(position);
        BlockVector3 orig = position.subtract(this.from);
        BlockVector3 transformed = this.transform.apply(orig.toVector3()).toBlockPoint().add(this.to);
        return this.destination.setBiome(transformed, biome);
    }

    @Override
    @Deprecated
    public boolean apply(BlockVector2 position) throws WorldEditException {
        boolean success = false;
        for (int y = this.destination.getMinimumPoint().y(); y <= this.destination.getMaximumPoint().y(); ++y) {
            success |= this.apply(position.toBlockVector3(y));
        }
        return success;
    }
}

