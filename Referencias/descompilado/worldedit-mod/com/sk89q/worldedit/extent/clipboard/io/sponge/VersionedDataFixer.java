/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent.clipboard.io.sponge;

import com.sk89q.worldedit.world.DataFixer;
import javax.annotation.Nullable;

final class VersionedDataFixer {
    private final int dataVersion;
    @Nullable
    private final DataFixer fixer;

    VersionedDataFixer(int dataVersion, @Nullable DataFixer fixer) {
        this.dataVersion = dataVersion;
        this.fixer = fixer;
    }

    public boolean isActive() {
        return this.fixer != null;
    }

    public <T> T fixUp(DataFixer.FixType<T> type, T original) {
        if (!this.isActive()) {
            return original;
        }
        return this.fixer.fixUp(type, original, this.dataVersion);
    }
}

