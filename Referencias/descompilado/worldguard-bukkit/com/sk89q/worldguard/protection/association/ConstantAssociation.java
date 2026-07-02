/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.association;

import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;

class ConstantAssociation
implements RegionAssociable {
    private final Association association;

    ConstantAssociation(Association association) {
        this.association = association;
    }

    @Override
    public Association getAssociation(List<ProtectedRegion> regions) {
        return this.association;
    }
}

