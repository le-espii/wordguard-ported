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
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.mask.SplatterMask2D;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;

public class SplatterMask
extends AbstractMask {
    private final BlockVector3 position;
    private final double decay;
    private final double size;
    private final double sizeSq;

    public SplatterMask(BlockVector3 position, double decay, double size) {
        Preconditions.checkArgument((decay >= 0.0 ? 1 : 0) != 0, (Object)"decay must be >= 0");
        Preconditions.checkArgument((decay <= 1.0 ? 1 : 0) != 0, (Object)"decay must be <= 1");
        this.position = position;
        this.decay = decay;
        this.size = size;
        this.sizeSq = size * size;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        double distSq = vector.distanceSq(this.position);
        double distRatio = distSq / this.sizeSq;
        double decayChance = distRatio * this.decay * 2.0;
        return ThreadLocalRandom.current().nextDouble() > decayChance;
    }

    @Override
    @Nullable
    public Mask2D toMask2D() {
        return new SplatterMask2D(this.position.toBlockVector2(), this.decay, this.size);
    }
}

