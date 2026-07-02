/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.generation.ConfiguredFeatureType;

public class FeatureGenerator
implements RegionFunction {
    private final ConfiguredFeatureType featureType;
    private final EditSession editSession;

    public FeatureGenerator(EditSession editSession, ConfiguredFeatureType featureType) {
        this.editSession = editSession;
        this.featureType = featureType;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        return this.editSession.getWorld().generateFeature(this.featureType, this.editSession, position);
    }
}

