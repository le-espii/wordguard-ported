/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 */
package com.sk89q.worldedit.world.snapshot.experimental;

import com.google.common.collect.ComparisonChain;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;

public final class SnapshotInfo
implements Comparable<SnapshotInfo> {
    private final URI name;
    private final ZonedDateTime dateTime;

    public static SnapshotInfo create(URI name, ZonedDateTime dateTime) {
        return new SnapshotInfo(name, dateTime);
    }

    private SnapshotInfo(URI name, ZonedDateTime dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public URI getName() {
        return this.name;
    }

    public String getDisplayName() {
        if (this.name.getScheme().equals("snapfs")) {
            return this.name.getSchemeSpecificPart();
        }
        return this.name.toString();
    }

    public ZonedDateTime getDateTime() {
        return this.dateTime;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SnapshotInfo that = (SnapshotInfo)o;
        return Objects.equals(this.name, that.name) && Objects.equals(this.dateTime, that.dateTime);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.dateTime);
    }

    public String toString() {
        return "SnapshotInfo{name='" + String.valueOf(this.name) + "',date=" + String.valueOf(this.dateTime) + "}";
    }

    @Override
    public int compareTo(SnapshotInfo o) {
        return ComparisonChain.start().compare((Comparable)this.dateTime, (Comparable)o.dateTime).compare((Comparable)this.name, (Comparable)o.name).result();
    }
}

