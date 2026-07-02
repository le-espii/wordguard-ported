/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.mask.AbstractMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.mask.OffsetsMask2D;
import com.sk89q.worldedit.math.BlockVector3;
import javax.annotation.Nullable;

@Deprecated
public class OffsetMask
extends AbstractMask {
    private Mask mask;
    private BlockVector3 offset;

    public OffsetMask(Mask mask, BlockVector3 offset) {
        Preconditions.checkNotNull((Object)mask);
        Preconditions.checkNotNull((Object)((Object)offset));
        this.mask = mask;
        this.offset = offset;
    }

    public Mask getMask() {
        return this.mask;
    }

    public void setMask(Mask mask) {
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    public BlockVector3 getOffset() {
        return this.offset;
    }

    public void setOffset(BlockVector3 offset) {
        Preconditions.checkNotNull((Object)((Object)offset));
        this.offset = offset;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        return this.getMask().test(vector.add(this.offset));
    }

    @Override
    @Nullable
    public Mask2D toMask2D() {
        Mask2D childMask = this.getMask().toMask2D();
        if (childMask != null) {
            return OffsetsMask2D.single(childMask, this.getOffset().toBlockVector2());
        }
        return null;
    }
}

