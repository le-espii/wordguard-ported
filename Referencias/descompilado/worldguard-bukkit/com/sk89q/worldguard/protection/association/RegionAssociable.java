/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.association;

import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;

public interface RegionAssociable {
    public Association getAssociation(List<ProtectedRegion> var1);
}

