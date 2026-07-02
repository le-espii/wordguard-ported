/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.factory;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.generator.FeatureGenerator;
import com.sk89q.worldedit.function.mask.NoiseFilter;
import com.sk89q.worldedit.math.noise.RandomNoise;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;

public final class FeatureGeneratorFactory
implements Contextual<RegionMaskingFilter> {
    private final ConfiguredFeatureType type;
    private final double density;

    public FeatureGeneratorFactory(ConfiguredFeatureType type, double density) {
        this.type = type;
        this.density = density;
    }

    @Override
    public RegionMaskingFilter createFromContext(EditContext input) {
        return new RegionMaskingFilter(new NoiseFilter(new RandomNoise(), this.density), new FeatureGenerator((EditSession)input.getDestination(), this.type));
    }

    public String toString() {
        return "feature of type " + String.valueOf(this.type);
    }
}

