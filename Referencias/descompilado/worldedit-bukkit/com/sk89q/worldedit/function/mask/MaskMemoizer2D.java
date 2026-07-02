/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.mask;

import com.sk89q.worldedit.function.mask.AbstractMask2D;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.util.collection.BlockMap;

public class MaskMemoizer2D
extends AbstractMask2D
implements AutoCloseable {
    private final Mask2D mask;
    private final BlockMap<Boolean> cache;

    public MaskMemoizer2D(Mask2D mask) {
        this.mask = mask;
        this.cache = BlockMap.create();
    }

    @Override
    public boolean test(BlockVector2 vector) {
        return this.cache.computeIfAbsent(vector.toBlockVector3(), ignored -> this.mask.test(vector));
    }

    public void clear() {
        this.cache.clear();
    }

    @Override
    public void close() {
        this.clear();
    }
}

