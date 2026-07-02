/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.managers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionDifference;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.managers.index.ConcurrentRegionIndex;
import com.sk89q.worldguard.protection.managers.storage.DifferenceSaveException;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.util.Normal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class RegionManager {
    private final RegionDatabase store;
    private final Function<String, ? extends ConcurrentRegionIndex> indexFactory;
    private final FlagRegistry flagRegistry;
    private ConcurrentRegionIndex index;

    public RegionManager(RegionDatabase store, Function<String, ? extends ConcurrentRegionIndex> indexFactory, FlagRegistry flagRegistry) {
        Preconditions.checkNotNull((Object)store);
        Preconditions.checkNotNull(indexFactory);
        Preconditions.checkNotNull((Object)flagRegistry, (Object)"flagRegistry");
        this.store = store;
        this.indexFactory = indexFactory;
        this.index = indexFactory.apply(store.getName());
        this.flagRegistry = flagRegistry;
    }

    public String getName() {
        return this.store.getName();
    }

    public void load() throws StorageException {
        Set<ProtectedRegion> regions = this.store.loadAll(this.flagRegistry);
        for (ProtectedRegion region : regions) {
            region.setDirty(false);
        }
        this.setRegions(regions);
    }

    public void save() throws StorageException {
        this.index.setDirty(false);
        this.store.saveAll(new HashSet<ProtectedRegion>(this.getFilteredValuesCopy()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean saveChanges() throws StorageException {
        RegionDifference diff = this.index.getAndClearDifference();
        boolean successful = false;
        try {
            if (diff.containsChanges()) {
                try {
                    this.store.saveChanges(diff);
                }
                catch (DifferenceSaveException e) {
                    this.save();
                }
                successful = true;
                boolean bl = true;
                return bl;
            }
            successful = true;
            boolean bl = false;
            return bl;
        }
        finally {
            if (!successful) {
                this.index.setDirty(diff);
            }
        }
    }

    public void loadChunk(BlockVector2 position) {
        this.index.bias(position);
    }

    public void loadChunks(Collection<BlockVector2> positions) {
        this.index.biasAll(positions);
    }

    public void unloadChunk(BlockVector2 position) {
        this.index.forget(position);
    }

    public Map<String, ProtectedRegion> getRegions() {
        HashMap<String, ProtectedRegion> map = new HashMap<String, ProtectedRegion>();
        for (ProtectedRegion region : this.index.values()) {
            map.put(Normal.normalize(region.getId()), region);
        }
        return Collections.unmodifiableMap(map);
    }

    public void setRegions(Map<String, ProtectedRegion> regions) {
        Preconditions.checkNotNull(regions);
        this.setRegions(regions.values());
    }

    public void setRegions(Collection<ProtectedRegion> regions) {
        Preconditions.checkNotNull(regions);
        ConcurrentRegionIndex newIndex = this.indexFactory.apply(this.getName());
        newIndex.addAll(regions);
        newIndex.getAndClearDifference();
        this.index = newIndex;
    }

    public void addRegion(ProtectedRegion region) {
        Preconditions.checkNotNull((Object)region);
        this.index.add(region);
    }

    public boolean hasRegion(String id) {
        return this.index.contains(id);
    }

    @Nullable
    public ProtectedRegion getRegion(String id) {
        Preconditions.checkNotNull((Object)id);
        return this.index.get(id);
    }

    @Nullable
    @Deprecated
    public ProtectedRegion matchRegion(String pattern) {
        return this.getRegion(pattern);
    }

    @Nullable
    public Set<ProtectedRegion> removeRegion(String id) {
        return this.removeRegion(id, RemovalStrategy.REMOVE_CHILDREN);
    }

    @Nullable
    public Set<ProtectedRegion> removeRegion(String id, RemovalStrategy strategy) {
        return this.index.remove(id, strategy);
    }

    public ApplicableRegionSet getApplicableRegions(BlockVector3 position) {
        return this.getApplicableRegions(position, RegionQuery.QueryOption.COMPUTE_PARENTS);
    }

    public ApplicableRegionSet getApplicableRegions(BlockVector3 position, RegionQuery.QueryOption option) {
        Preconditions.checkNotNull((Object)position);
        Preconditions.checkNotNull((Object)((Object)option));
        HashSet regions = Sets.newHashSet();
        this.index.applyContaining(position, (Predicate<ProtectedRegion>)((Object)option.createIndexConsumer(regions)));
        return new RegionResultSet(option.constructResult(regions), this.index.get("__global__"), true);
    }

    public ApplicableRegionSet getApplicableRegions(ProtectedRegion region) {
        return this.getApplicableRegions(region, RegionQuery.QueryOption.COMPUTE_PARENTS);
    }

    public ApplicableRegionSet getApplicableRegions(ProtectedRegion region, RegionQuery.QueryOption option) {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)((Object)option));
        HashSet regions = Sets.newHashSet();
        this.index.applyIntersecting(region, (Predicate<ProtectedRegion>)((Object)option.createIndexConsumer(regions)));
        return new RegionResultSet(option.constructResult(regions), this.index.get("__global__"), true);
    }

    public List<String> getApplicableRegionsIDs(BlockVector3 position) {
        Preconditions.checkNotNull((Object)position);
        ArrayList<String> names = new ArrayList<String>();
        this.index.applyContaining(position, region -> names.add(region.getId()));
        return names;
    }

    public boolean overlapsUnownedRegion(ProtectedRegion region, LocalPlayer player) {
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)player);
        ConcurrentRegionIndex index = this.index;
        AtomicBoolean overlapsUnowned = new AtomicBoolean();
        index.applyIntersecting(region, test -> {
            if (!test.getOwners().contains(player)) {
                overlapsUnowned.set(true);
                return false;
            }
            return true;
        });
        return overlapsUnowned.get();
    }

    public int size() {
        return this.index.size();
    }

    public int getRegionCountOfPlayer(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        AtomicInteger count = new AtomicInteger();
        this.index.apply(test -> {
            if (test.getOwners().contains(player)) {
                count.incrementAndGet();
            }
            return true;
        });
        return count.get();
    }

    private List<ProtectedRegion> getFilteredValuesCopy() {
        ArrayList<ProtectedRegion> filteredValues = new ArrayList<ProtectedRegion>();
        for (ProtectedRegion region : this.index.values()) {
            if (region.isTransient()) continue;
            filteredValues.add(region);
        }
        return filteredValues;
    }
}

