/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.snapshot.experimental;

import com.sk89q.worldedit.world.snapshot.experimental.Snapshot;
import java.util.Comparator;

public class SnapshotComparator {
    private static final Comparator<Snapshot> COMPARATOR = Comparator.comparing(Snapshot::getInfo);

    public static Comparator<Snapshot> getInstance() {
        return COMPARATOR;
    }

    private SnapshotComparator() {
    }
}

