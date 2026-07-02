/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.pattern;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.pattern.AbstractPattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPattern
extends AbstractPattern {
    private final Random random = new Random();
    private final List<Chance> patterns = new ArrayList<Chance>();
    private double max = 0.0;

    public void add(Pattern pattern, double chance) {
        Preconditions.checkNotNull((Object)pattern);
        this.patterns.add(new Chance(pattern, chance));
        this.max += chance;
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        double r = this.random.nextDouble();
        double offset = 0.0;
        for (Chance chance : this.patterns) {
            if (r <= (offset + chance.chance()) / this.max) {
                return chance.pattern().applyBlock(position);
            }
            offset += chance.chance();
        }
        throw new RuntimeException("ProportionalFillPattern");
    }

    private record Chance(Pattern pattern, double chance) {
    }
}

