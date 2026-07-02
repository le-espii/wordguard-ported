/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.math.BlockVector3;

public class ExistingBlockMask
extends AbstractExtentMask {
    public ExistingBlockMask(Extent extent) {
        super(extent);
    }

    @Override
    public boolean test(BlockVector3 vector) {
        return !this.getExtent().getBlock(vector).getBlockType().getMaterial().isAir();
    }
}

