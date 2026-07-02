/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractMask2D;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.world.biome.BiomeType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public class BiomeMask2D
extends AbstractMask2D {
    private final Extent extent;
    private final Set<BiomeType> biomes = new HashSet<BiomeType>();

    public BiomeMask2D(Extent extent, Collection<BiomeType> biomes) {
        Preconditions.checkNotNull((Object)extent);
        Preconditions.checkNotNull(biomes);
        this.extent = extent;
        this.biomes.addAll(biomes);
    }

    public BiomeMask2D(Extent extent, BiomeType ... biome) {
        this(extent, Arrays.asList((BiomeType[])Preconditions.checkNotNull((Object)biome)));
    }

    public void add(Collection<BiomeType> biomes) {
        Preconditions.checkNotNull(biomes);
        this.biomes.addAll(biomes);
    }

    public void add(BiomeType ... biome) {
        this.add(Arrays.asList((BiomeType[])Preconditions.checkNotNull((Object)biome)));
    }

    public Collection<BiomeType> getBiomes() {
        return this.biomes;
    }

    @Override
    public boolean test(BlockVector2 vector) {
        BiomeType biome = this.extent.getBiome(vector);
        return this.biomes.contains(biome);
    }
}

