/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Region;

public interface FlatRegion
extends Region {
    public int getMinimumY();

    public int getMaximumY();

    public Iterable<BlockVector2> asFlatRegion();
}

