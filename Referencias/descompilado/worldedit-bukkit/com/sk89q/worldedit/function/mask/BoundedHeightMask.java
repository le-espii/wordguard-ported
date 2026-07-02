/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.mask.AbstractMask;
import com.sk89q.worldedit.math.BlockVector3;

public class BoundedHeightMask
extends AbstractMask {
    private final int minY;
    private final int maxY;

    public BoundedHeightMask(int minY, int maxY) {
        Preconditions.checkArgument((minY <= maxY ? 1 : 0) != 0, (Object)"minY <= maxY required");
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        return vector.y() >= this.minY && vector.y() <= this.maxY;
    }
}

