/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.math.BlockVector3;

public interface RegionFunction {
    public boolean apply(BlockVector3 var1) throws WorldEditException;
}

