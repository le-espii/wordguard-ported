/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.function.mask;

import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.math.BlockVector3;
import javax.annotation.Nullable;

@FunctionalInterface
public interface Mask {
    public boolean test(BlockVector3 var1);

    @Nullable
    default public Mask2D toMask2D() {
        return null;
    }
}

