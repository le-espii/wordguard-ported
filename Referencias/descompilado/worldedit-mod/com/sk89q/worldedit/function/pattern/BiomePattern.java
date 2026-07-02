/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.pattern;

import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;

public interface BiomePattern {
    @Deprecated
    default public BiomeType apply(BlockVector2 position) {
        return this.applyBiome(position.toBlockVector3());
    }

    @NonAbstractForCompatibility(delegateName="apply", delegateParams={BlockVector2.class})
    default public BiomeType applyBiome(BlockVector3 position) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.apply(position.toBlockVector2());
    }
}

