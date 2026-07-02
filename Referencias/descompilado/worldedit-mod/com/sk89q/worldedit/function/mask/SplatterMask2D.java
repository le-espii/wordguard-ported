/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.mask.AbstractMask2D;
import com.sk89q.worldedit.math.BlockVector2;
import java.util.concurrent.ThreadLocalRandom;

public class SplatterMask2D
extends AbstractMask2D {
    private final BlockVector2 position;
    private final double decay;
    private final double sizeSq;

    public SplatterMask2D(BlockVector2 position, double decay, double size) {
        Preconditions.checkArgument((decay >= 0.0 ? 1 : 0) != 0, (Object)"decay must be >= 0");
        Preconditions.checkArgument((decay <= 1.0 ? 1 : 0) != 0, (Object)"decay must be <= 1");
        this.position = position;
        this.decay = decay;
        this.sizeSq = size * size;
    }

    @Override
    public boolean test(BlockVector2 vector) {
        double distSq = vector.distanceSq(this.position);
        double distRatio = distSq / this.sizeSq;
        double decayChance = distRatio * this.decay * 2.0;
        return ThreadLocalRandom.current().nextDouble() > decayChance;
    }
}

