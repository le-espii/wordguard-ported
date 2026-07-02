/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.mask.AbstractMask2D;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.math.BlockVector2;

@Deprecated
public class OffsetMask2D
extends AbstractMask2D {
    private Mask2D mask;
    private BlockVector2 offset;

    public OffsetMask2D(Mask2D mask, BlockVector2 offset) {
        Preconditions.checkNotNull((Object)mask);
        Preconditions.checkNotNull((Object)((Object)offset));
        this.mask = mask;
        this.offset = offset;
    }

    public Mask2D getMask() {
        return this.mask;
    }

    public void setMask(Mask2D mask) {
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    public BlockVector2 getOffset() {
        return this.offset;
    }

    public void setOffset(BlockVector2 offset) {
        Preconditions.checkNotNull((Object)((Object)offset));
        this.offset = offset;
    }

    @Override
    public boolean test(BlockVector2 vector) {
        return this.getMask().test(vector.add(this.offset));
    }
}

