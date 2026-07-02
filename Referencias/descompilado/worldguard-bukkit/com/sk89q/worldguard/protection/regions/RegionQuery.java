/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.regions;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.FailedLoadRegionSet;
import com.sk89q.worldguard.protection.PermissiveRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.MapFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.QueryCache;
import com.sk89q.worldguard.protection.util.NormativeOrders;
import com.sk89q.worldguard.protection.util.RegionCollectionConsumer;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class RegionQuery {
    private final ConfigurationManager config;
    private final QueryCache cache;

    public RegionQuery(QueryCache cache) {
        Preconditions.checkNotNull((Object)cache);
        this.config = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
        this.cache = cache;
    }

    public ApplicableRegionSet getApplicableRegions(Location location) {
        return this.getApplicableRegions(location, QueryOption.COMPUTE_PARENTS);
    }

    public ApplicableRegionSet getApplicableRegions(Location location, QueryOption option) {
        Preconditions.checkNotNull((Object)location);
        Preconditions.checkNotNull((Object)((Object)option));
        World world = (World)location.getExtent();
        WorldConfiguration worldConfig = this.config.get(world);
        if (!worldConfig.useRegions) {
            return PermissiveRegionSet.getInstance();
        }
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get((World)location.getExtent());
        if (manager != null) {
            return this.cache.queryContains(manager, location, option);
        }
        return FailedLoadRegionSet.getInstance();
    }

    public boolean testBuild(Location location, LocalPlayer player, StateFlag ... flag) {
        if (flag.length == 0) {
            return this.testState(location, player, Flags.BUILD);
        }
        return StateFlag.test(StateFlag.combine(StateFlag.denyToNone(this.queryState(location, player, Flags.BUILD)), this.queryState(location, player, flag)));
    }

    public boolean testBuild(Location location, RegionAssociable associable, StateFlag ... flag) {
        if (flag.length == 0) {
            return this.testState(location, associable, Flags.BUILD);
        }
        return StateFlag.test(StateFlag.combine(StateFlag.denyToNone(this.queryState(location, associable, Flags.BUILD)), this.queryState(location, associable, flag)));
    }

    public <K> boolean testBuild(Location location, RegionAssociable associable, MapFlag<K, StateFlag.State> mapFlag, K key, @Nullable StateFlag fallback, StateFlag ... flag) {
        if (mapFlag == null) {
            return this.testBuild(location, associable, flag);
        }
        if (flag.length == 0) {
            return StateFlag.test(StateFlag.combine(StateFlag.denyToNone(this.queryState(location, associable, Flags.BUILD)), this.queryMapValue(location, associable, mapFlag, key, fallback)));
        }
        return StateFlag.test(StateFlag.combine(StateFlag.denyToNone(this.queryState(location, associable, Flags.BUILD)), this.queryMapValue(location, associable, mapFlag, key, fallback), this.queryState(location, associable, flag)));
    }

    public boolean testState(Location location, @Nullable LocalPlayer player, StateFlag ... flag) {
        return StateFlag.test(this.queryState(location, player, flag));
    }

    public boolean testState(Location location, @Nullable RegionAssociable associable, StateFlag ... flag) {
        return StateFlag.test(this.queryState(location, associable, flag));
    }

    @Nullable
    public StateFlag.State queryState(Location location, @Nullable LocalPlayer player, StateFlag ... flags) {
        return this.getApplicableRegions(location).queryState(player, flags);
    }

    @Nullable
    public StateFlag.State queryState(Location location, @Nullable RegionAssociable associable, StateFlag ... flags) {
        return this.getApplicableRegions(location).queryState(associable, flags);
    }

    @Nullable
    public <V> V queryValue(Location location, @Nullable LocalPlayer player, Flag<V> flag) {
        return this.getApplicableRegions(location).queryValue(player, flag);
    }

    @Nullable
    public <V> V queryValue(Location location, @Nullable RegionAssociable associable, Flag<V> flag) {
        return this.getApplicableRegions(location).queryValue(associable, flag);
    }

    @Nullable
    public <V, K> V queryMapValue(Location location, @Nullable RegionAssociable subject, MapFlag<K, V> flag, K key) {
        return this.getApplicableRegions(location).queryMapValue(subject, flag, key);
    }

    @Nullable
    public <V, K> V queryMapValue(Location location, @Nullable RegionAssociable subject, MapFlag<K, V> flag, K key, Flag<V> fallback) {
        return this.getApplicableRegions(location).queryMapValue(subject, flag, key, fallback);
    }

    public <V> Collection<V> queryAllValues(Location location, @Nullable LocalPlayer player, Flag<V> flag) {
        return this.getApplicableRegions(location).queryAllValues(player, flag);
    }

    public <V> Collection<V> queryAllValues(Location location, @Nullable RegionAssociable associable, Flag<V> flag) {
        return this.getApplicableRegions(location).queryAllValues(associable, flag);
    }

    public static enum QueryOption {
        NONE(false){

            @Override
            public List<ProtectedRegion> constructResult(Set<ProtectedRegion> applicable) {
                return ImmutableList.copyOf(applicable);
            }

            @Override
            Map<QueryOption, ApplicableRegionSet> createCache(RegionManager manager, Location location, Map<QueryOption, ApplicableRegionSet> cache) {
                if (cache == null) {
                    cache = new EnumMap<QueryOption, ApplicableRegionSet>(QueryOption.class);
                    cache.put(NONE, manager.getApplicableRegions(location.toVector().toBlockPoint(), NONE));
                }
                return cache;
            }
        }
        ,
        SORT(false){

            @Override
            Map<QueryOption, ApplicableRegionSet> createCache(RegionManager manager, Location location, Map<QueryOption, ApplicableRegionSet> cache) {
                if (cache == null) {
                    EnumMap<QueryOption, ApplicableRegionSet> newCache = new EnumMap<QueryOption, ApplicableRegionSet>(QueryOption.class);
                    ApplicableRegionSet result = manager.getApplicableRegions(location.toVector().toBlockPoint(), SORT);
                    newCache.put(NONE, result);
                    newCache.put(SORT, result);
                    return newCache;
                }
                cache.computeIfAbsent(SORT, k -> new RegionResultSet(((ApplicableRegionSet)cache.get((Object)NONE)).getRegions(), manager.getRegion("__global__")));
                return cache;
            }
        }
        ,
        COMPUTE_PARENTS(true){

            @Override
            Map<QueryOption, ApplicableRegionSet> createCache(RegionManager manager, Location location, Map<QueryOption, ApplicableRegionSet> cache) {
                if (cache == null) {
                    EnumMap<QueryOption, ApplicableRegionSet> newCache = new EnumMap<QueryOption, ApplicableRegionSet>(QueryOption.class);
                    ApplicableRegionSet noParResult = manager.getApplicableRegions(location.toVector().toBlockPoint(), NONE);
                    Set<ProtectedRegion> noParRegions = noParResult.getRegions();
                    HashSet<ProtectedRegion> regions = new HashSet<ProtectedRegion>();
                    noParRegions.forEach(new RegionCollectionConsumer(regions, true)::apply);
                    RegionResultSet result = new RegionResultSet(regions, manager.getRegion("__global__"));
                    if (regions.size() == noParRegions.size()) {
                        newCache.put(NONE, result);
                        newCache.put(SORT, result);
                    } else {
                        newCache.put(NONE, noParResult);
                    }
                    newCache.put(COMPUTE_PARENTS, result);
                    return newCache;
                }
                cache.computeIfAbsent(COMPUTE_PARENTS, k -> {
                    HashSet<ProtectedRegion> regions = new HashSet<ProtectedRegion>();
                    ApplicableRegionSet result = (ApplicableRegionSet)cache.get((Object)SORT);
                    boolean sorted = true;
                    if (result == null) {
                        result = (ApplicableRegionSet)cache.get((Object)NONE);
                        sorted = false;
                    }
                    Set<ProtectedRegion> noParRegions = result.getRegions();
                    noParRegions.forEach(new RegionCollectionConsumer(regions, true)::apply);
                    if (sorted && regions.size() == noParRegions.size()) {
                        return result;
                    }
                    result = new RegionResultSet(regions, manager.getRegion("__global__"));
                    if (regions.size() == noParRegions.size()) {
                        cache.put(SORT, result);
                    }
                    return result;
                });
                return cache;
            }
        };

        private final boolean collectParents;

        private QueryOption(boolean collectParents) {
            this.collectParents = collectParents;
        }

        public RegionCollectionConsumer createIndexConsumer(Collection<? super ProtectedRegion> collection) {
            return new RegionCollectionConsumer(collection, this.collectParents);
        }

        public List<ProtectedRegion> constructResult(Set<ProtectedRegion> applicable) {
            return NormativeOrders.fromSet(applicable);
        }

        abstract Map<QueryOption, ApplicableRegionSet> createCache(RegionManager var1, Location var2, Map<QueryOption, ApplicableRegionSet> var3);
    }
}

