/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.AbstractRegionSet;
import com.sk89q.worldguard.protection.FlagValueCalculator;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.MapFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.NormativeOrders;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class RegionResultSet
extends AbstractRegionSet {
    private final List<ProtectedRegion> applicable;
    private final FlagValueCalculator flagValueCalculator;
    @Nullable
    private Set<ProtectedRegion> regionSet;

    public RegionResultSet(List<ProtectedRegion> applicable, @Nullable ProtectedRegion globalRegion) {
        this(applicable, globalRegion, false);
    }

    public RegionResultSet(Set<ProtectedRegion> applicable, @Nullable ProtectedRegion globalRegion) {
        this(NormativeOrders.fromSet(applicable), globalRegion, true);
        this.regionSet = ImmutableSet.copyOf(applicable);
    }

    public RegionResultSet(List<ProtectedRegion> applicable, @Nullable ProtectedRegion globalRegion, boolean sorted) {
        Preconditions.checkNotNull(applicable);
        if (!sorted) {
            NormativeOrders.sort(applicable);
        }
        this.applicable = Collections.unmodifiableList(applicable);
        this.flagValueCalculator = new FlagValueCalculator(applicable, globalRegion);
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    @Nullable
    public StateFlag.State queryState(@Nullable RegionAssociable subject, StateFlag ... flags) {
        return this.flagValueCalculator.queryState(subject, flags);
    }

    @Override
    @Nullable
    public <V> V queryValue(@Nullable RegionAssociable subject, Flag<V> flag) {
        return this.flagValueCalculator.queryValue(subject, flag);
    }

    @Override
    public <V> Collection<V> queryAllValues(@Nullable RegionAssociable subject, Flag<V> flag) {
        return this.flagValueCalculator.queryAllValues(subject, flag);
    }

    @Override
    @Nullable
    public <V, K> V queryMapValue(@Nullable RegionAssociable subject, MapFlag<K, V> flag, K key) {
        return this.flagValueCalculator.queryMapValue(subject, flag, key, null);
    }

    @Override
    @Nullable
    public <V, K> V queryMapValue(@Nullable RegionAssociable subject, MapFlag<K, V> flag, K key, Flag<V> fallback) {
        return this.flagValueCalculator.queryMapValue(subject, flag, key, fallback);
    }

    @Override
    public boolean isOwnerOfAll(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        for (ProtectedRegion region : this.applicable) {
            if (region.isOwner(player)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isMemberOfAll(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        for (ProtectedRegion region : this.applicable) {
            if (region.isMember(player)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        return this.applicable.size();
    }

    @Override
    public Set<ProtectedRegion> getRegions() {
        if (this.regionSet != null) {
            return this.regionSet;
        }
        this.regionSet = ImmutableSet.copyOf(this.applicable);
        return this.regionSet;
    }

    @Override
    public Iterator<ProtectedRegion> iterator() {
        return this.applicable.iterator();
    }

    public static RegionResultSet fromSortedList(List<ProtectedRegion> regions, @Nullable ProtectedRegion globalRegion) {
        return new RegionResultSet(regions, globalRegion, true);
    }
}

