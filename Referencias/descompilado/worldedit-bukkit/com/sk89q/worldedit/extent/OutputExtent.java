/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import javax.annotation.Nullable;

public interface OutputExtent {
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 var1, T var2) throws WorldEditException;

    default public boolean fullySupports3DBiomes() {
        return true;
    }

    @Deprecated
    default public boolean setBiome(BlockVector2 position, BiomeType biome) {
        return this.setBiome(position.toBlockVector3(), biome);
    }

    @NonAbstractForCompatibility(delegateName="setBiome", delegateParams={BlockVector3.class, BiomeType.class})
    default public boolean setBiome(BlockVector3 position, BiomeType biome) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.setBiome(position.toBlockVector2(), biome);
    }

    @Nullable
    public Operation commit();
}

