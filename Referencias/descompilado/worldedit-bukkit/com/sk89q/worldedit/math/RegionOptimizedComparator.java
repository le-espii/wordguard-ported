/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.RegionOptimizedChunkComparator;
import java.util.Comparator;

public class RegionOptimizedComparator {
    public static final Comparator<BlockVector3> INSTANCE = RegionOptimizedChunkComparator.INSTANCE.thenComparing(BlockVector3.sortByCoordsYzx());

    private RegionOptimizedComparator() {
    }
}

