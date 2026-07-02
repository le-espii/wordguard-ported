/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.managers;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collections;
import java.util.Set;

public final class RegionDifference {
    private final Set<ProtectedRegion> changed;
    private final Set<ProtectedRegion> removed;

    public RegionDifference(Set<ProtectedRegion> changed, Set<ProtectedRegion> removed) {
        Preconditions.checkNotNull(changed);
        Preconditions.checkNotNull(removed);
        this.changed = changed;
        this.removed = removed;
    }

    public Set<ProtectedRegion> getChanged() {
        return Collections.unmodifiableSet(this.changed);
    }

    public Set<ProtectedRegion> getRemoved() {
        return Collections.unmodifiableSet(this.removed);
    }

    public boolean containsChanges() {
        return !this.changed.isEmpty() || !this.removed.isEmpty();
    }

    public void addAll(RegionDifference diff) {
        this.changed.addAll(diff.getChanged());
        this.removed.addAll(diff.getRemoved());
    }
}

