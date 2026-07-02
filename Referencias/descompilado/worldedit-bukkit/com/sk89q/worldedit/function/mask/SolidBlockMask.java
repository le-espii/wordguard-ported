/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;

public class SolidBlockMask
extends AbstractExtentMask {
    public SolidBlockMask(Extent extent) {
        super(extent);
    }

    @Override
    public boolean test(BlockVector3 vector) {
        Extent extent = this.getExtent();
        BlockState block = extent.getBlock(vector);
        return block.getBlockType().getMaterial().isMovementBlocker();
    }
}

