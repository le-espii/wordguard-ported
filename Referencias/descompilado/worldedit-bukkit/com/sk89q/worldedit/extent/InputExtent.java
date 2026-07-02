/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent;

import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;

public interface InputExtent {
    public BlockState getBlock(BlockVector3 var1);

    public BaseBlock getFullBlock(BlockVector3 var1);

    @Deprecated
    default public BiomeType getBiome(BlockVector2 position) {
        return this.getBiome(position.toBlockVector3());
    }

    @NonAbstractForCompatibility(delegateName="getBiome", delegateParams={BlockVector2.class})
    default public BiomeType getBiome(BlockVector3 position) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.getBiome(position.toBlockVector2());
    }
}

