/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;

public class RegionMaskingFilter
implements RegionFunction {
    private final RegionFunction function;
    private final Mask mask;

    public RegionMaskingFilter(Mask mask, RegionFunction function) {
        Preconditions.checkNotNull((Object)function);
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
        this.function = function;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        return this.mask.test(position) && this.function.apply(position);
    }
}

