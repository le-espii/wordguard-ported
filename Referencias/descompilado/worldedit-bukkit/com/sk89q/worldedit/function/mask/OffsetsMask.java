/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.function.mask.AbstractMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.mask.OffsetsMask2D;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import javax.annotation.Nullable;

public class OffsetsMask
extends AbstractMask {
    private static final ImmutableSet<BlockVector3> OFFSET_LIST = (ImmutableSet)Direction.valuesOf(Direction.Flag.CARDINAL | Direction.Flag.UPRIGHT).stream().map(Direction::toBlockVector).collect(ImmutableSet.toImmutableSet());
    private final Mask mask;
    private final boolean excludeSelf;
    private final int minMatches;
    private final int maxMatches;
    private final ImmutableSet<BlockVector3> offsets;

    public static OffsetsMask single(Mask mask, BlockVector3 offset) {
        return OffsetsMask.builder(mask).maxMatches(1).offsets((Iterable<BlockVector3>)ImmutableList.of((Object)((Object)offset))).build();
    }

    public static Builder builder(Mask mask) {
        return new Builder().mask(mask);
    }

    private OffsetsMask(Mask mask, boolean excludeSelf, int minMatches, int maxMatches, ImmutableSet<BlockVector3> offsets) {
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

    public Mask getMask() {
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

    public ImmutableSet<BlockVector3> getOffsets() {
        return this.offsets;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        if (this.excludeSelf && this.mask.test(vector)) {
            return false;
        }
        int matches = 0;
        for (BlockVector3 offset : this.offsets) {
            if (!this.mask.test(vector.add(offset)) || ++matches <= this.maxMatches) continue;
            return false;
        }
        return this.minMatches <= matches && matches <= this.maxMatches;
    }

    @Override
    @Nullable
    public Mask2D toMask2D() {
        Mask2D childMask = this.getMask().toMask2D();
        if (childMask != null) {
            return OffsetsMask2D.builder(childMask).excludeSelf(this.excludeSelf).minMatches(this.minMatches).maxMatches(this.maxMatches).offsets(Iterables.transform(this.offsets, BlockVector3::toBlockVector2)).build();
        }
        return null;
    }

    public static final class Builder {
        private Mask mask;
        private boolean excludeSelf;
        private int minMatches = 1;
        private int maxMatches = Integer.MAX_VALUE;
        private ImmutableSet<BlockVector3> offsets = OFFSET_LIST;

        private Builder() {
        }

        public Builder mask(Mask mask) {
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

        public Builder offsets(Iterable<BlockVector3> offsets) {
            this.offsets = ImmutableSet.copyOf(offsets);
            return this;
        }

        public OffsetsMask build() {
            return new OffsetsMask(this.mask, this.excludeSelf, this.minMatches, this.maxMatches, this.offsets);
        }
    }
}

