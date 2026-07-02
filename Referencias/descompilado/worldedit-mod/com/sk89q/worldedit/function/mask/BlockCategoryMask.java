/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategory;

public class BlockCategoryMask
extends AbstractExtentMask {
    private final BlockCategory category;

    public BlockCategoryMask(Extent extent, BlockCategory category) {
        super(extent);
        Preconditions.checkNotNull((Object)category);
        this.category = category;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        return this.category.contains(this.getExtent().getBlock(vector));
    }
}

