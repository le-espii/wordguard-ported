/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.mask;

import com.sk89q.worldedit.function.mask.AbstractMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.mask.MaskMemoizer2D;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.BlockMap;

public class MaskMemoizer
extends AbstractMask
implements AutoCloseable {
    private final Mask mask;
    private final BlockMap<Boolean> cache;

    public MaskMemoizer(Mask mask) {
        this.mask = mask;
        this.cache = BlockMap.create();
    }

    @Override
    public boolean test(BlockVector3 vector) {
        return this.cache.computeIfAbsent(vector, this.mask::test);
    }

    public void clear() {
        this.cache.clear();
    }

    @Override
    public Mask2D toMask2D() {
        return new MaskMemoizer2D(this.mask.toMask2D());
    }

    @Override
    public void close() {
        this.clear();
    }
}

