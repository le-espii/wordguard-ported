/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.managers.index;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionDifference;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.managers.index.AbstractRegionIndex;
import com.sk89q.worldguard.protection.managers.index.ConcurrentRegionIndex;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.Normal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import javax.annotation.Nullable;

public class HashMapIndex
extends AbstractRegionIndex
implements ConcurrentRegionIndex {
    private final ConcurrentMap<String, ProtectedRegion> regions = new ConcurrentHashMap<String, ProtectedRegion>();
    private Set<ProtectedRegion> removed = new HashSet<ProtectedRegion>();
    private final Object lock = new Object();

    protected void rebuildIndex() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void performAdd(ProtectedRegion region) {
        Preconditions.checkNotNull((Object)region);
        region.setDirty(true);
        Object object = this.lock;
        synchronized (object) {
            String normalId = Normal.normalize(region.getId());
            ProtectedRegion existing = (ProtectedRegion)this.regions.get(normalId);
            if (existing != null) {
                this.removeAndReplaceParents(existing.getId(), RemovalStrategy.UNSET_PARENT_IN_CHILDREN, region, false);
                if (existing.getId().equals(region.getId())) {
                    this.removed.remove(existing);
                }
            }
            this.regions.put(normalId, region);
            this.removed.remove(region);
            ProtectedRegion parent = region.getParent();
            if (parent != null) {
                this.performAdd(parent);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAll(Collection<ProtectedRegion> regions) {
        Preconditions.checkNotNull(regions);
        Object object = this.lock;
        synchronized (object) {
            for (ProtectedRegion region : regions) {
                this.performAdd(region);
            }
            this.rebuildIndex();
        }
    }

    @Override
    public void bias(BlockVector2 chunkPosition) {
    }

    @Override
    public void biasAll(Collection<BlockVector2> chunkPositions) {
    }

    @Override
    public void forget(BlockVector2 chunkPosition) {
    }

    @Override
    public void forgetAll() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(ProtectedRegion region) {
        Object object = this.lock;
        synchronized (object) {
            this.performAdd(region);
            this.rebuildIndex();
        }
    }

    @Override
    public Set<ProtectedRegion> remove(String id, RemovalStrategy strategy) {
        return this.removeAndReplaceParents(id, strategy, null, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<ProtectedRegion> removeAndReplaceParents(String id, RemovalStrategy strategy, @Nullable ProtectedRegion replacement, boolean rebuildIndex) {
        Preconditions.checkNotNull((Object)id);
        Preconditions.checkNotNull((Object)((Object)strategy));
        HashSet<ProtectedRegion> removedSet = new HashSet<ProtectedRegion>();
        Object object = this.lock;
        synchronized (object) {
            ProtectedRegion removed = (ProtectedRegion)this.regions.remove(Normal.normalize(id));
            if (removed != null) {
                int lastSize;
                removedSet.add(removed);
                do {
                    lastSize = removedSet.size();
                    Iterator it = this.regions.values().iterator();
                    while (it.hasNext()) {
                        ProtectedRegion current = (ProtectedRegion)it.next();
                        ProtectedRegion parent = current.getParent();
                        if (parent == null || !removedSet.contains(parent)) continue;
                        switch (strategy) {
                            case REMOVE_CHILDREN: {
                                removedSet.add(current);
                                it.remove();
                                break;
                            }
                            case UNSET_PARENT_IN_CHILDREN: {
                                try {
                                    current.setParent(replacement);
                                    break;
                                }
                                catch (ProtectedRegion.CircularInheritanceException e) {
                                    WorldGuard.logger.log(Level.WARNING, "Failed to replace parent '" + parent.getId() + "' of child '" + current.getId() + "' with replacement '" + replacement.getId() + "'", e);
                                    current.clearParent();
                                }
                            }
                        }
                    }
                } while (strategy != RemovalStrategy.UNSET_PARENT_IN_CHILDREN && removedSet.size() != lastSize);
            }
            this.removed.addAll(removedSet);
            if (rebuildIndex) {
                this.rebuildIndex();
            }
        }
        return removedSet;
    }

    @Override
    public boolean contains(String id) {
        return this.regions.containsKey(Normal.normalize(id));
    }

    @Override
    @Nullable
    public ProtectedRegion get(String id) {
        return (ProtectedRegion)this.regions.get(Normal.normalize(id));
    }

    @Override
    public void apply(Predicate<ProtectedRegion> consumer) {
        ProtectedRegion region;
        Iterator iterator = this.regions.values().iterator();
        while (iterator.hasNext() && consumer.test(region = (ProtectedRegion)iterator.next())) {
        }
    }

    @Override
    public void applyContaining(BlockVector3 position, Predicate<ProtectedRegion> consumer) {
        this.apply(region -> !region.contains(position) || consumer.test((ProtectedRegion)region));
    }

    @Override
    public void applyIntersecting(ProtectedRegion region, Predicate<ProtectedRegion> consumer) {
        for (ProtectedRegion found : region.getIntersectingRegions(this.regions.values())) {
            if (!consumer.test(found)) break;
        }
    }

    @Override
    public int size() {
        return this.regions.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RegionDifference getAndClearDifference() {
        Object object = this.lock;
        synchronized (object) {
            HashSet<ProtectedRegion> changed = new HashSet<ProtectedRegion>();
            Set<ProtectedRegion> removed = this.removed;
            for (ProtectedRegion region : this.regions.values()) {
                if (!region.isDirty()) continue;
                changed.add(region);
                region.setDirty(false);
            }
            this.removed = new HashSet<ProtectedRegion>();
            return new RegionDifference(changed, removed);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDirty(RegionDifference difference) {
        Object object = this.lock;
        synchronized (object) {
            for (ProtectedRegion changed : difference.getChanged()) {
                changed.setDirty(true);
            }
            this.removed.addAll(difference.getRemoved());
        }
    }

    @Override
    public Collection<ProtectedRegion> values() {
        return Collections.unmodifiableCollection(this.regions.values());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isDirty() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.removed.isEmpty()) {
                return true;
            }
            for (ProtectedRegion region : this.regions.values()) {
                if (!region.isDirty()) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDirty(boolean dirty) {
        Object object = this.lock;
        synchronized (object) {
            if (!dirty) {
                this.removed.clear();
            }
            for (ProtectedRegion region : this.regions.values()) {
                region.setDirty(dirty);
            }
        }
    }

    public static final class Factory
    implements Function<String, HashMapIndex> {
        @Override
        public HashMapIndex apply(String name) {
            return new HashMapIndex();
        }
    }
}

