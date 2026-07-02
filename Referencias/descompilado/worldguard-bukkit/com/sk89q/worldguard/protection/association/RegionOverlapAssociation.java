/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.sk89q.worldguard.protection.association;

import com.sk89q.worldguard.protection.association.AbstractRegionOverlapAssociation;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Set;
import javax.annotation.Nonnull;

public class RegionOverlapAssociation
extends AbstractRegionOverlapAssociation {
    public RegionOverlapAssociation(@Nonnull Set<ProtectedRegion> source) {
        this(source, false);
    }

    public RegionOverlapAssociation(@Nonnull Set<ProtectedRegion> source, boolean useMaxPriorityAssociation) {
        super(source, useMaxPriorityAssociation);
        this.calcMaxPriority();
    }
}

