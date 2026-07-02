/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world;

import com.sk89q.worldedit.world.RegenOptions;
import java.util.OptionalLong;

final class AutoValue_RegenOptions
extends RegenOptions {
    private final OptionalLong seed;
    private final boolean regenBiomes;

    private AutoValue_RegenOptions(OptionalLong seed, boolean regenBiomes) {
        this.seed = seed;
        this.regenBiomes = regenBiomes;
    }

    @Override
    public OptionalLong getSeed() {
        return this.seed;
    }

    @Override
    boolean isRegenBiomes() {
        return this.regenBiomes;
    }

    public String toString() {
        return "RegenOptions{seed=" + String.valueOf(this.seed) + ", regenBiomes=" + this.regenBiomes + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RegenOptions) {
            RegenOptions that = (RegenOptions)o;
            return this.seed.equals(that.getSeed()) && this.regenBiomes == that.isRegenBiomes();
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.seed.hashCode();
        h$ *= 1000003;
        return h$ ^= this.regenBiomes ? 1231 : 1237;
    }

    static final class Builder
    extends RegenOptions.Builder {
        private OptionalLong seed = OptionalLong.empty();
        private boolean regenBiomes;
        private byte set$0;

        Builder() {
        }

        @Override
        RegenOptions.Builder seed(OptionalLong seed) {
            if (seed == null) {
                throw new NullPointerException("Null seed");
            }
            this.seed = seed;
            return this;
        }

        @Override
        public RegenOptions.Builder regenBiomes(boolean regenBiomes) {
            this.regenBiomes = regenBiomes;
            this.set$0 = (byte)(this.set$0 | 1);
            return this;
        }

        @Override
        public RegenOptions build() {
            if (this.set$0 != 1) {
                String missing = " regenBiomes";
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_RegenOptions(this.seed, this.regenBiomes);
        }
    }
}

