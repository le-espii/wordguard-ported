/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.block;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.World;

public class ApplySideEffect
implements RegionFunction {
    private final World world;
    private final SideEffectSet sideEffectSet;

    public ApplySideEffect(World world, SideEffectSet sideEffectSet) {
        Preconditions.checkNotNull((Object)world);
        Preconditions.checkNotNull((Object)sideEffectSet);
        this.world = world;
        this.sideEffectSet = sideEffectSet;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        this.world.applySideEffects(position, this.world.getBlock(position), this.sideEffectSet);
        return true;
    }
}

