/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.math.BlockVector3;

public class RegionOffset
implements RegionFunction {
    private BlockVector3 offset;
    private final RegionFunction function;

    public RegionOffset(BlockVector3 offset, RegionFunction function) {
        Preconditions.checkNotNull((Object)function);
        this.setOffset(offset);
        this.function = function;
    }

    public BlockVector3 getOffset() {
        return this.offset;
    }

    public void setOffset(BlockVector3 offset) {
        Preconditions.checkNotNull((Object)((Object)offset));
        this.offset = offset;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        return this.function.apply(position.add(this.offset));
    }
}

