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
import com.sk89q.worldedit.function.mask.MaskMemoizer;
import com.sk89q.worldedit.function.mask.MaskMemoizer2D;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import javax.annotation.Nullable;

public final class Masks {
    private static final AlwaysTrue ALWAYS_TRUE = new AlwaysTrue();
    private static final AlwaysFalse ALWAYS_FALSE = new AlwaysFalse();

    private Masks() {
    }

    public static Mask alwaysTrue() {
        return ALWAYS_TRUE;
    }

    public static Mask2D alwaysTrue2D() {
        return ALWAYS_TRUE;
    }

    public static Mask memoize(Mask mask) {
        Preconditions.checkNotNull((Object)mask);
        return new MaskMemoizer(mask);
    }

    public static Mask2D memoize(Mask2D mask) {
        Preconditions.checkNotNull((Object)mask);
        return new MaskMemoizer2D(mask);
    }

    public static Mask negate(Mask mask) {
        if (mask instanceof AlwaysTrue) {
            return ALWAYS_FALSE;
        }
        if (mask instanceof AlwaysFalse) {
            return ALWAYS_TRUE;
        }
        if (mask instanceof NegatedMask) {
            return ((NegatedMask)mask).mask;
        }
        Preconditions.checkNotNull((Object)mask);
        return new NegatedMask(mask);
    }

    public static Mask2D negate(Mask2D mask) {
        if (mask instanceof AlwaysTrue) {
            return ALWAYS_FALSE;
        }
        if (mask instanceof AlwaysFalse) {
            return ALWAYS_TRUE;
        }
        if (mask instanceof NegatedMask2D) {
            return ((NegatedMask2D)mask).mask;
        }
        Preconditions.checkNotNull((Object)mask);
        return new NegatedMask2D(mask);
    }

    public static Mask asMask(final Mask2D mask) {
        return new AbstractMask(){

            @Override
            public boolean test(BlockVector3 vector) {
                return mask.test(vector.toBlockVector2());
            }

            @Override
            @Nullable
            public Mask2D toMask2D() {
                return mask;
            }
        };
    }

    private static class AlwaysTrue
    implements Mask,
    Mask2D {
        private AlwaysTrue() {
        }

        @Override
        public boolean test(BlockVector3 vector) {
            return true;
        }

        @Override
        public boolean test(BlockVector2 vector) {
            return true;
        }

        @Override
        @Nullable
        public Mask2D toMask2D() {
            return this;
        }
    }

    private static class AlwaysFalse
    implements Mask,
    Mask2D {
        private AlwaysFalse() {
        }

        @Override
        public boolean test(BlockVector3 vector) {
            return false;
        }

        @Override
        public boolean test(BlockVector2 vector) {
            return false;
        }

        @Override
        @Nullable
        public Mask2D toMask2D() {
            return this;
        }
    }

    private record NegatedMask(Mask mask) implements Mask
    {
        @Override
        public boolean test(BlockVector3 vector) {
            return !this.mask.test(vector);
        }

        @Override
        @Nullable
        public Mask2D toMask2D() {
            Mask2D mask2D = this.mask.toMask2D();
            if (mask2D == null) {
                return null;
            }
            return Masks.negate(mask2D);
        }
    }

    private record NegatedMask2D(Mask2D mask) implements Mask2D
    {
        @Override
        public boolean test(BlockVector2 vector) {
            return !this.mask.test(vector);
        }
    }
}

