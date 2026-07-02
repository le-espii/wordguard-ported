/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.util.Location
 */
package com.sk89q.worldguard.protection.association;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.AbstractRegionOverlapAssociation;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.List;

public class DelayedRegionOverlapAssociation
extends AbstractRegionOverlapAssociation {
    private final RegionQuery query;
    private final Location location;

    public DelayedRegionOverlapAssociation(RegionQuery query, Location location) {
        this(query, location, false);
    }

    public DelayedRegionOverlapAssociation(RegionQuery query, Location location, boolean useMaxPriorityAssociation) {
        super(null, useMaxPriorityAssociation);
        Preconditions.checkNotNull((Object)query);
        Preconditions.checkNotNull((Object)location);
        this.query = query;
        this.location = location;
    }

    @Override
    public Association getAssociation(List<ProtectedRegion> regions) {
        if (this.source == null) {
            ApplicableRegionSet result = this.query.getApplicableRegions(this.location, RegionQuery.QueryOption.NONE);
            this.source = result.getRegions();
            this.calcMaxPriority();
        }
        return super.getAssociation(regions);
    }
}

