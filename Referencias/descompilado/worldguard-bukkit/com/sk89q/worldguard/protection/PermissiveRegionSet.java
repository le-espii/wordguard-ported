/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.AbstractRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.MapFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class PermissiveRegionSet
extends AbstractRegionSet {
    private static final PermissiveRegionSet INSTANCE = new PermissiveRegionSet();

    private PermissiveRegionSet() {
    }

    @Override
    public boolean isVirtual() {
        return true;
    }

    @Override
    @Nullable
    public <V> V queryValue(@Nullable RegionAssociable subject, Flag<V> flag) {
        if (flag == Flags.BUILD) {
            return (V)((Object)StateFlag.State.DENY);
        }
        return flag.getDefault();
    }

    @Override
    @Nullable
    public <V, K> V queryMapValue(@Nullable RegionAssociable subject, MapFlag<K, V> flag, K key) {
        return this.queryMapValue(subject, flag, key, null);
    }

    @Override
    @Nullable
    public <V, K> V queryMapValue(@Nullable RegionAssociable subject, MapFlag<K, V> flag, K key, @Nullable Flag<V> fallback) {
        Map defaultVal = (Map)flag.getDefault();
        return (V)(defaultVal != null ? defaultVal.get(key) : (fallback != null ? fallback.getDefault() : null));
    }

    @Override
    public <V> Collection<V> queryAllValues(@Nullable RegionAssociable subject, Flag<V> flag) {
        if (flag == Flags.BUILD) {
            return ImmutableList.of((Object)((Object)StateFlag.State.DENY));
        }
        V fallback = flag.getDefault();
        return fallback != null ? ImmutableList.of(fallback) : ImmutableList.of();
    }

    @Override
    public boolean isOwnerOfAll(LocalPlayer player) {
        return true;
    }

    @Override
    public boolean isMemberOfAll(LocalPlayer player) {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<ProtectedRegion> getRegions() {
        return Collections.emptySet();
    }

    @Override
    public Iterator<ProtectedRegion> iterator() {
        return Collections.emptyIterator();
    }

    public static PermissiveRegionSet getInstance() {
        return INSTANCE;
    }
}

