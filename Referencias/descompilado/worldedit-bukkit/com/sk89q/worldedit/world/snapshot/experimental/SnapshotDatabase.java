/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.snapshot.experimental;

import com.sk89q.worldedit.util.collection.MoreStreams;
import com.sk89q.worldedit.world.snapshot.experimental.Snapshot;
import com.sk89q.worldedit.world.snapshot.experimental.SnapshotComparator;
import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

public interface SnapshotDatabase {
    public String getScheme();

    public Optional<Snapshot> getSnapshot(URI var1) throws IOException;

    public Stream<Snapshot> getSnapshots(String var1) throws IOException;

    default public Stream<Snapshot> getSnapshotsNewestFirst(String worldName) throws IOException {
        return this.getSnapshots(worldName).sorted(SnapshotComparator.getInstance().reversed());
    }

    default public Stream<Snapshot> getSnapshotsOldestFirst(String worldName) throws IOException {
        return this.getSnapshots(worldName).sorted(SnapshotComparator.getInstance());
    }

    default public Stream<Snapshot> getSnapshotsBefore(String worldName, ZonedDateTime date) throws IOException {
        return MoreStreams.takeWhile(this.getSnapshotsOldestFirst(worldName), snap -> snap.getInfo().getDateTime().isBefore(date));
    }

    default public Stream<Snapshot> getSnapshotsAfter(String worldName, ZonedDateTime date) throws IOException {
        return MoreStreams.takeWhile(this.getSnapshotsNewestFirst(worldName), snap -> snap.getInfo().getDateTime().isAfter(date));
    }
}

