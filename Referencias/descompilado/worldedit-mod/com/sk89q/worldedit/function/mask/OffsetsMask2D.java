/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.function.mask.AbstractMask2D;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;

public class OffsetsMask2D
extends AbstractMask2D {
    private static final ImmutableSet<BlockVector2> OFFSET_LIST = (ImmutableSet)Direction.valuesOf(Direction.Flag.CARDINAL).stream().map(Direction::toBlockVector).map(BlockVector3::toBlockVector2).collect(ImmutableSet.toImmutableSet());
    private final Mask2D mask;
    private final boolean excludeSelf;
    private final int minMatches;
    private final int maxMatches;
    private final ImmutableSet<BlockVector2> offsets;

    public static OffsetsMask2D single(Mask2D mask, BlockVector2 offset) {
        return OffsetsMask2D.builder(mask).maxMatches(1).offsets((Iterable<BlockVector2>)ImmutableList.of((Object)((Object)offset))).build();
    }

    public static Builder builder(Mask2D mask) {
        return new Builder().mask(mask);
    }

    private OffsetsMask2D(Mask2D mask, boolean excludeSelf, int minMatches, int maxMatches, ImmutableSet<BlockVector2> offsets) {
        Preconditions.checkNotNull((Object)mask);
        Preconditions.checkNotNull(offsets);
        Preconditions.checkArgument((minMatches <= maxMatches ? 1 : 0) != 0, (Object)"minMatches must be less than or equal to maxMatches");
        Preconditions.checkArgument((minMatches >= 0 ? 1 : 0) != 0, (Object)"minMatches must be greater than or equal to 0");
        Preconditions.checkArgument((minMatches <= offsets.size() ? 1 : 0) != 0, (Object)"minMatches must be less than or equal to the number of offsets");
        Preconditions.checkArgument((offsets.size() > 0 ? 1 : 0) != 0, (Object)"offsets must have at least one element");
        this.mask = mask;
        this.excludeSelf = excludeSelf;
        this.minMatches = minMatches;
        this.maxMatches = maxMatches;
        this.offsets = offsets;
    }

    public Mask2D getMask() {
        return this.mask;
    }

    public boolean getExcludeSelf() {
        return this.excludeSelf;
    }

    public int getMinMatches() {
        return this.minMatches;
    }

    public int getMaxMatches() {
        return this.maxMatches;
    }

    public ImmutableSet<BlockVector2> getOffsets() {
        return this.offsets;
    }

    @Override
    public boolean test(BlockVector2 vector) {
        if (this.excludeSelf && this.mask.test(vector)) {
            return false;
        }
        int matches = 0;
        for (BlockVector2 offset : this.offsets) {
            if (!this.mask.test(vector.add(offset)) || ++matches <= this.maxMatches) continue;
            return false;
        }
        return this.minMatches <= matches && matches <= this.maxMatches;
    }

    public static final class Builder {
        private Mask2D mask;
        private boolean excludeSelf;
        private int minMatches = 1;
        private int maxMatches = Integer.MAX_VALUE;
        private ImmutableSet<BlockVector2> offsets = OFFSET_LIST;

        private Builder() {
        }

        public Builder mask(Mask2D mask) {
            this.mask = mask;
            return this;
        }

        public Builder excludeSelf(boolean excludeSelf) {
            this.excludeSelf = excludeSelf;
            return this;
        }

        public Builder minMatches(int minMatches) {
            this.minMatches = minMatches;
            return this;
        }

        public Builder maxMatches(int maxMatches) {
            this.maxMatches = maxMatches;
            return this;
        }

        public Builder offsets(Iterable<BlockVector2> offsets) {
            this.offsets = ImmutableSet.copyOf(offsets);
            return this;
        }

        public OffsetsMask2D build() {
            return new OffsetsMask2D(this.mask, this.excludeSelf, this.minMatches, this.maxMatches, this.offsets);
        }
    }
}

