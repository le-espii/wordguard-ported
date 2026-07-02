/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.MapFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class FlagValueCalculator {
    @Nullable
    private final ProtectedRegion globalRegion;
    private final Iterable<ProtectedRegion> applicable;

    public FlagValueCalculator(List<ProtectedRegion> regions, @Nullable ProtectedRegion globalRegion) {
        Preconditions.checkNotNull(regions);
        this.globalRegion = globalRegion;
        this.applicable = globalRegion == null ? regions : Iterables.concat(regions, Collections.singletonList(globalRegion));
    }

    private Iterable<ProtectedRegion> getApplicable() {
        return this.applicable;
    }

    public Result getMembership(RegionAssociable subject) {
        ProtectedRegion region;
        int priority;
        Preconditions.checkNotNull((Object)subject);
        int minimumPriority = Integer.MIN_VALUE;
        Result result = Result.NO_REGIONS;
        HashSet ignoredRegions = Sets.newHashSet();
        Iterator<ProtectedRegion> iterator = this.getApplicable().iterator();
        while (iterator.hasNext() && (priority = this.getPriority(region = iterator.next())) >= minimumPriority) {
            if (this.getEffectiveFlag(region, Flags.PASSTHROUGH, subject) == StateFlag.State.ALLOW || ignoredRegions.contains(region)) continue;
            minimumPriority = priority;
            boolean member = RegionGroup.MEMBERS.contains(subject.getAssociation(Collections.singletonList(region)));
            if (member) {
                result = Result.SUCCESS;
                this.addParents(ignoredRegions, region);
                continue;
            }
            return Result.FAIL;
        }
        return result;
    }

    @Nullable
    public StateFlag.State queryState(@Nullable RegionAssociable subject, StateFlag ... flags) {
        StateFlag.State value = null;
        for (StateFlag flag : flags) {
            if ((value = StateFlag.combine(value, this.queryValue(subject, flag))) == StateFlag.State.DENY) break;
        }
        return value;
    }

    @Nullable
    public StateFlag.State queryState(@Nullable RegionAssociable subject, StateFlag flag) {
        return this.queryValue(subject, flag);
    }

    @Nullable
    public <V> V queryValue(@Nullable RegionAssociable subject, Flag<V> flag) {
        Collection<V> values = this.queryAllValues(subject, flag, true);
        return flag.chooseValue(values);
    }

    @Nullable
    public <V, K> V queryMapValue(@Nullable RegionAssociable subject, MapFlag<K, V> flag, K key, Flag<V> fallback) {
        ProtectedRegion region;
        int priority;
        Preconditions.checkNotNull(flag);
        Preconditions.checkNotNull(key);
        HashMap<ProtectedRegion, V> consideredValues = new HashMap<ProtectedRegion, V>();
        HashMap<ProtectedRegion, V> fallbackValues = new HashMap<ProtectedRegion, V>();
        int minimumPriority = Integer.MIN_VALUE;
        HashSet<ProtectedRegion> ignoredParents = new HashSet<ProtectedRegion>();
        Iterator<ProtectedRegion> iterator = this.getApplicable().iterator();
        while (iterator.hasNext() && (priority = this.getPriority(region = iterator.next())) >= minimumPriority) {
            if (ignoredParents.contains(region)) continue;
            V effectiveValue = this.getEffectiveMapValue(region, flag, key, subject);
            if (effectiveValue != null) {
                minimumPriority = priority;
                consideredValues.put(region, effectiveValue);
            } else if (fallback != null && (effectiveValue = this.getEffectiveFlag(region, fallback, subject)) != null) {
                minimumPriority = priority;
                fallbackValues.put(region, effectiveValue);
            }
            this.addParents(ignoredParents, region);
        }
        if (consideredValues.isEmpty()) {
            if (fallback != null && !fallbackValues.isEmpty()) {
                return fallback.chooseValue(fallbackValues.values());
            }
            V defaultValue = flag.getValueFlag().getDefault();
            return (V)(defaultValue != null ? defaultValue : (fallback != null ? fallback.getDefault() : null));
        }
        return flag.getValueFlag().chooseValue(consideredValues.values());
    }

    @Nullable
    public <V, K> V getEffectiveMapValue(ProtectedRegion region, MapFlag<K, V> mapFlag, K key, RegionAssociable subject) {
        return FlagValueCalculator.getEffectiveMapValueOf(region, mapFlag, key, subject);
    }

    @Nullable
    public static <V, K> V getEffectiveMapValueOf(ProtectedRegion region, MapFlag<K, V> mapFlag, K key, RegionAssociable subject) {
        ArrayList<ProtectedRegion> seen = new ArrayList<ProtectedRegion>();
        for (ProtectedRegion current = region; current != null; current = current.getParent()) {
            seen.add(current);
            Map mapValue = (Map)current.getFlag(mapFlag);
            if (mapValue == null || !mapValue.containsKey(key)) continue;
            boolean use = true;
            if (mapFlag.getRegionGroupFlag() != null) {
                RegionGroup group = (RegionGroup)((Object)current.getFlag(mapFlag.getRegionGroupFlag()));
                if (group == null) {
                    group = mapFlag.getRegionGroupFlag().getDefault();
                }
                if (group == null) {
                    use = false;
                } else if (subject == null) {
                    use = group.contains(Association.NON_MEMBER);
                } else if (!group.contains(subject.getAssociation(seen))) {
                    use = false;
                }
            }
            if (!use) continue;
            return mapValue.get(key);
        }
        return null;
    }

    public <V> Collection<V> queryAllValues(@Nullable RegionAssociable subject, Flag<V> flag) {
        return this.queryAllValues(subject, flag, false);
    }

    private <V> Collection<V> queryAllValues(@Nullable RegionAssociable subject, Flag<V> flag, boolean acceptOne) {
        ProtectedRegion region;
        int priority;
        Preconditions.checkNotNull(flag);
        if (acceptOne && flag.hasConflictStrategy()) {
            acceptOne = false;
        }
        if (flag.requiresSubject() && subject == null) {
            throw new NullPointerException("The " + flag.getName() + " flag is handled in a special fashion and requires a non-null subject parameter");
        }
        int minimumPriority = Integer.MIN_VALUE;
        HashMap<ProtectedRegion, V> consideredValues = new HashMap<ProtectedRegion, V>();
        HashSet<ProtectedRegion> ignoredParents = new HashSet<ProtectedRegion>();
        Iterator<ProtectedRegion> iterator = this.getApplicable().iterator();
        while (iterator.hasNext() && (priority = this.getPriority(region = iterator.next())) >= minimumPriority) {
            if (ignoredParents.contains(region)) continue;
            V value = this.getEffectiveFlag(region, flag, subject);
            if (value != null) {
                minimumPriority = priority;
                if (acceptOne) {
                    return Arrays.asList(value);
                }
                consideredValues.put(region, value);
            }
            this.addParents(ignoredParents, region);
            if (priority == minimumPriority || !flag.implicitlySetWithMembership() || this.getEffectiveFlag(region, Flags.PASSTHROUGH, subject) == StateFlag.State.ALLOW) continue;
            minimumPriority = priority;
        }
        if (flag.usesMembershipAsDefault() && consideredValues.isEmpty()) {
            switch (this.getMembership(subject).ordinal()) {
                case 1: {
                    return ImmutableList.of();
                }
                case 2: {
                    return ImmutableList.of((Object)((Object)StateFlag.State.ALLOW));
                }
            }
        }
        if (consideredValues.isEmpty()) {
            V fallback = flag.getDefault();
            return fallback != null ? ImmutableList.of(fallback) : ImmutableList.of();
        }
        return consideredValues.values();
    }

    public int getPriority(ProtectedRegion region) {
        return FlagValueCalculator.getPriorityOf(region);
    }

    public static int getPriorityOf(ProtectedRegion region) {
        if (region.getId().equals("__global__")) {
            return Integer.MIN_VALUE;
        }
        return region.getPriority();
    }

    @Nullable
    public <V> V getEffectiveFlag(ProtectedRegion region, Flag<V> flag, @Nullable RegionAssociable subject) {
        return FlagValueCalculator.getEffectiveFlagOf(region, flag, subject);
    }

    @Nullable
    public static <V> V getEffectiveFlagOf(ProtectedRegion region, Flag<V> flag, @Nullable RegionAssociable subject) {
        if (region.getId().equals("__global__")) {
            if (flag == Flags.PASSTHROUGH) {
                StateFlag.State passthrough = (StateFlag.State)((Object)region.getFlag(Flags.PASSTHROUGH));
                if (passthrough == StateFlag.State.DENY || passthrough != StateFlag.State.ALLOW && region.hasMembersOrOwners()) {
                    return null;
                }
                return (V)((Object)StateFlag.State.ALLOW);
            }
            if (flag instanceof StateFlag && ((StateFlag)flag).preventsAllowOnGlobal()) {
                StateFlag.State value = (StateFlag.State)((Object)region.getFlag((StateFlag)flag));
                return (V)((Object)(value != StateFlag.State.ALLOW ? value : null));
            }
        }
        ArrayList<ProtectedRegion> seen = new ArrayList<ProtectedRegion>();
        for (ProtectedRegion current = region; current != null; current = current.getParent()) {
            seen.add(current);
            Object value = current.getFlag(flag);
            if (value == null) continue;
            boolean use = true;
            if (flag.getRegionGroupFlag() != null) {
                RegionGroup group = (RegionGroup)((Object)current.getFlag(flag.getRegionGroupFlag()));
                if (group == null) {
                    group = flag.getRegionGroupFlag().getDefault();
                }
                if (group == null) {
                    use = false;
                } else if (subject == null) {
                    use = group.contains(Association.NON_MEMBER);
                } else if (!group.contains(subject.getAssociation(seen))) {
                    use = false;
                }
            }
            if (!use) continue;
            return value;
        }
        return null;
    }

    private void addParents(Set<ProtectedRegion> ignored, ProtectedRegion region) {
        for (ProtectedRegion parent = region.getParent(); parent != null; parent = parent.getParent()) {
            ignored.add(parent);
        }
    }

    public static enum Result {
        NO_REGIONS,
        FAIL,
        SUCCESS;

    }
}

