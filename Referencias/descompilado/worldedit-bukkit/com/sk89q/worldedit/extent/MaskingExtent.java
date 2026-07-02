/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class MaskingExtent
extends AbstractDelegateExtent {
    private Mask mask;

    public MaskingExtent(Extent extent, Mask mask) {
        super(extent);
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    public Mask getMask() {
        return this.mask;
    }

    public void setMask(Mask mask) {
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        return this.mask.test(location) && super.setBlock(location, block);
    }
}

