/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world;

import com.google.auto.value.AutoValue;
import com.sk89q.worldedit.world.AutoValue_RegenOptions;
import java.util.OptionalLong;
import javax.annotation.Nullable;

@AutoValue
public abstract class RegenOptions {
    public static Builder builder() {
        return new AutoValue_RegenOptions.Builder().seed(OptionalLong.empty()).regenBiomes(false);
    }

    RegenOptions() {
    }

    public abstract OptionalLong getSeed();

    abstract boolean isRegenBiomes();

    public final boolean shouldRegenBiomes() {
        return this.isRegenBiomes();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public final Builder seed(@Nullable Long seed) {
            return this.seed(seed == null ? OptionalLong.empty() : OptionalLong.of(seed));
        }

        abstract Builder seed(OptionalLong var1);

        public abstract Builder regenBiomes(boolean var1);

        public abstract RegenOptions build();
    }
}

