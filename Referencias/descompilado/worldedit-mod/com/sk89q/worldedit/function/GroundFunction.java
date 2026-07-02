/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.LayerFunction;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;

public class GroundFunction
implements LayerFunction {
    private Mask mask;
    private final RegionFunction function;
    private int affected;

    public GroundFunction(Mask mask, RegionFunction function) {
        Preconditions.checkNotNull((Object)mask);
        Preconditions.checkNotNull((Object)function);
        this.mask = mask;
        this.function = function;
    }

    public Mask getMask() {
        return this.mask;
    }

    public void setMask(Mask mask) {
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    public int getAffected() {
        return this.affected;
    }

    @Override
    public boolean isGround(BlockVector3 position) {
        return this.mask.test(position);
    }

    @Override
    public boolean apply(BlockVector3 position, int depth) throws WorldEditException {
        if (depth == 0 && this.function.apply(position)) {
            ++this.affected;
            return true;
        }
        return false;
    }
}

