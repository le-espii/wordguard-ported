/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.MapFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;

public interface ApplicableRegionSet
extends Iterable<ProtectedRegion> {
    public boolean isVirtual();

    public boolean testState(@Nullable RegionAssociable var1, StateFlag ... var2);

    @Nullable
    public StateFlag.State queryState(@Nullable RegionAssociable var1, StateFlag ... var2);

    @Nullable
    public <V> V queryValue(@Nullable RegionAssociable var1, Flag<V> var2);

    @Nullable
    public <V, K> V queryMapValue(@Nullable RegionAssociable var1, MapFlag<K, V> var2, K var3);

    @Nullable
    public <V, K> V queryMapValue(@Nullable RegionAssociable var1, MapFlag<K, V> var2, K var3, @Nullable Flag<V> var4);

    public <V> Collection<V> queryAllValues(@Nullable RegionAssociable var1, Flag<V> var2);

    public boolean isOwnerOfAll(LocalPlayer var1);

    public boolean isMemberOfAll(LocalPlayer var1);

    public int size();

    public Set<ProtectedRegion> getRegions();
}

