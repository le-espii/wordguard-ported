/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.association;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.FlagValueCalculator;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class AbstractRegionOverlapAssociation
implements RegionAssociable {
    @Nullable
    protected Set<ProtectedRegion> source;
    private boolean useMaxPriorityAssociation;
    private int maxPriority;
    private Set<ProtectedRegion> maxPriorityRegions;

    protected AbstractRegionOverlapAssociation(@Nullable Set<ProtectedRegion> source, boolean useMaxPriorityAssociation) {
        this.source = source;
        this.useMaxPriorityAssociation = useMaxPriorityAssociation;
    }

    protected void calcMaxPriority() {
        Preconditions.checkNotNull(this.source);
        int best = 0;
        HashSet<ProtectedRegion> bestRegions = new HashSet<ProtectedRegion>();
        for (ProtectedRegion region : this.source) {
            int priority = region.getPriority();
            if (priority > best) {
                best = priority;
                bestRegions.clear();
                bestRegions.add(region);
                continue;
            }
            if (priority != best) continue;
            bestRegions.add(region);
        }
        this.maxPriority = best;
        this.maxPriorityRegions = bestRegions;
    }

    private boolean checkNonplayerProtectionDomains(Iterable<? extends ProtectedRegion> source, Collection<?> domains) {
        if (source == null || domains == null || domains.isEmpty()) {
            return false;
        }
        for (ProtectedRegion protectedRegion : source) {
            Set regionDomains = (Set)((Object)FlagValueCalculator.getEffectiveFlagOf(protectedRegion, Flags.NONPLAYER_PROTECTION_DOMAINS, this));
            if (regionDomains == null || regionDomains.isEmpty() || Collections.disjoint(regionDomains, domains)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Association getAssociation(List<ProtectedRegion> regions) {
        Preconditions.checkNotNull(this.source);
        Iterator<ProtectedRegion> iterator = regions.iterator();
        while (iterator.hasNext()) {
            for (ProtectedRegion region = iterator.next(); region != null; region = region.getParent()) {
                Set<ProtectedRegion> source;
                if (region.getId().equals("__global__") && this.source.isEmpty()) {
                    return Association.OWNER;
                }
                if (this.source.contains(region)) {
                    if (this.useMaxPriorityAssociation) {
                        int priority = region.getPriority();
                        if (priority == this.maxPriority) {
                            return Association.OWNER;
                        }
                    } else {
                        return Association.OWNER;
                    }
                }
                if (!this.checkNonplayerProtectionDomains(source = this.useMaxPriorityAssociation ? this.maxPriorityRegions : this.source, (Collection)((Object)FlagValueCalculator.getEffectiveFlagOf(region, Flags.NONPLAYER_PROTECTION_DOMAINS, this)))) continue;
                return Association.OWNER;
            }
        }
        return Association.NON_MEMBER;
    }
}

