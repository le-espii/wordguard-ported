/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.managers.storage;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;
import java.util.logging.Logger;

public final class RegionDatabaseUtils {
    private static final Logger log = Logger.getLogger(RegionDatabaseUtils.class.getCanonicalName());

    private RegionDatabaseUtils() {
    }

    public static void relinkParents(Map<String, ProtectedRegion> regions, Map<ProtectedRegion, String> parentSets) {
        Preconditions.checkNotNull(regions);
        Preconditions.checkNotNull(parentSets);
        for (Map.Entry<ProtectedRegion, String> entry : parentSets.entrySet()) {
            ProtectedRegion target = entry.getKey();
            ProtectedRegion parent = regions.get(entry.getValue());
            if (parent != null) {
                try {
                    target.setParent(parent);
                }
                catch (ProtectedRegion.CircularInheritanceException e) {
                    log.warning("Circular inheritance detected! Can't set the parent of '" + String.valueOf(target) + "' to parent '" + parent.getId() + "'");
                }
                continue;
            }
            log.warning("Unknown region parent: " + entry.getValue());
        }
    }
}

