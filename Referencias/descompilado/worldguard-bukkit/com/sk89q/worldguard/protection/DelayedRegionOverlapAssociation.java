/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 */
package com.sk89q.worldguard.protection;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.protection.regions.RegionQuery;

@Deprecated
public class DelayedRegionOverlapAssociation
extends com.sk89q.worldguard.protection.association.DelayedRegionOverlapAssociation {
    public DelayedRegionOverlapAssociation(RegionQuery query, Location location) {
        super(query, location, false);
    }
}

