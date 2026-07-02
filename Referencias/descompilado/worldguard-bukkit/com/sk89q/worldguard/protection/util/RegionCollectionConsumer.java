/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 */
package com.sk89q.worldguard.protection.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collection;

public class RegionCollectionConsumer
implements Predicate<ProtectedRegion> {
    private final Collection<? super ProtectedRegion> collection;
    private final boolean addParents;

    public RegionCollectionConsumer(Collection<? super ProtectedRegion> collection, boolean addParents) {
        Preconditions.checkNotNull(collection);
        this.collection = collection;
        this.addParents = addParents;
    }

    public boolean apply(ProtectedRegion region) {
        this.collection.add(region);
        if (this.addParents) {
            for (ProtectedRegion parent = region.getParent(); parent != null; parent = parent.getParent()) {
                this.collection.add(parent);
            }
        }
        return true;
    }
}

