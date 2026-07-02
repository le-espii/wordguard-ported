/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.block;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.math.BlockVector3;

public class Counter
implements RegionFunction {
    private int count;

    public int getCount() {
        return this.count;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        ++this.count;
        return false;
    }
}

