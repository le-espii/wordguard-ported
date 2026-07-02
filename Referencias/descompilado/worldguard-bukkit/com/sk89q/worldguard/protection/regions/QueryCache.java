/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.protection.regions;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class QueryCache {
    private final ConcurrentMap<CacheKey, Map<RegionQuery.QueryOption, ApplicableRegionSet>> cache = new ConcurrentHashMap<CacheKey, Map<RegionQuery.QueryOption, ApplicableRegionSet>>(16, 0.75f, 2);

    public ApplicableRegionSet queryContains(RegionManager manager, Location location, RegionQuery.QueryOption option) {
        Preconditions.checkNotNull((Object)manager);
        Preconditions.checkNotNull((Object)location);
        Preconditions.checkNotNull((Object)((Object)option));
        CacheKey key = new CacheKey(location);
        return (ApplicableRegionSet)this.cache.compute(key, (k, v) -> option.createCache(manager, location, (Map<RegionQuery.QueryOption, ApplicableRegionSet>)v)).get((Object)option);
    }

    public void invalidateAll() {
        this.cache.clear();
    }

    private static class CacheKey {
        private final World world;
        private final int x;
        private final int y;
        private final int z;
        private final int hashCode;

        private CacheKey(Location location) {
            this.world = (World)location.getExtent();
            this.x = location.getBlockX();
            this.y = location.getBlockY();
            this.z = location.getBlockZ();
            int result = this.world.hashCode();
            result = 31 * result + this.x;
            result = 31 * result + this.y;
            this.hashCode = result = 31 * result + this.z;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CacheKey cacheKey = (CacheKey)o;
            if (this.x != cacheKey.x) {
                return false;
            }
            if (this.y != cacheKey.y) {
                return false;
            }
            if (this.z != cacheKey.z) {
                return false;
            }
            return this.world.equals((Object)cacheKey.world);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }
}

