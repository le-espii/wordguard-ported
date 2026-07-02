/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.biome;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.util.WeightedChoice;
import com.sk89q.worldedit.util.function.LevenshteinDistance;
import com.sk89q.worldedit.world.biome.BiomeName;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;

@Deprecated
public final class Biomes {
    private Biomes() {
    }

    @Deprecated
    @Nullable
    public static BiomeType findBiomeByName(Collection<BiomeType> biomes, String name, BiomeRegistry registry) {
        Preconditions.checkNotNull(biomes);
        Preconditions.checkNotNull((Object)name);
        Preconditions.checkNotNull((Object)registry);
        LevenshteinDistance compare = new LevenshteinDistance(name, false, LevenshteinDistance.STANDARD_CHARS);
        WeightedChoice<BiomeType> chooser = new WeightedChoice<BiomeType>((java.util.function.Function<BiomeType, Number>)Functions.compose(((java.util.function.Function)((Object)compare))::apply, (Function)new BiomeName(registry)), 0.0);
        for (BiomeType biome : biomes) {
            chooser.consider(biome);
        }
        Optional choice = chooser.getChoice();
        if (choice.isPresent() && choice.get().getScore() <= 1.0) {
            return (BiomeType)choice.get().getValue();
        }
        return null;
    }
}

