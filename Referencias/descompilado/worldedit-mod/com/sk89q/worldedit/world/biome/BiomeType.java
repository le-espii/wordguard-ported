/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.biome;

import com.sk89q.worldedit.function.pattern.BiomePattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;

public record BiomeType(String id) implements Keyed,
BiomePattern
{
    public static final NamespacedRegistry<BiomeType> REGISTRY = new NamespacedRegistry("biome type", true);

    public String toString() {
        return this.id();
    }

    @Override
    public BiomeType applyBiome(BlockVector3 position) {
        return this;
    }
}

