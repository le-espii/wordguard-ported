/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.managers.index;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionDifference;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.managers.index.ConcurrentRegionIndex;
import com.sk89q.worldguard.protection.managers.index.RegionIndex;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.RegionCollectionConsumer;
import com.sk89q.worldguard.util.collect.LongHashTable;
import com.sk89q.worldguard.util.concurrent.EvenMoreExecutors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class ChunkHashTable
implements ConcurrentRegionIndex {
    private final String name;
    private ListeningExecutorService executor = this.createExecutor();
    private LongHashTable<ChunkState> states = new LongHashTable();
    private final RegionIndex index;
    private final Object lock = new Object();
    @Nullable
    private ChunkState lastState;

    public ChunkHashTable(RegionIndex index, String name) {
        Preconditions.checkNotNull((Object)index);
        this.index = index;
        this.name = name;
    }

    private ListeningExecutorService createExecutor() {
        return MoreExecutors.listeningDecorator((ExecutorService)EvenMoreExecutors.newBoundedCachedThreadPool(0, 4, Integer.MAX_VALUE, "WorldGuard Region Chunk Table - " + this.name));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private ChunkState get(BlockVector2 position, boolean create) {
        ChunkState state;
        Object object = this.lock;
        synchronized (object) {
            state = this.states.get(position.x(), position.z());
            if (state == null && create) {
                state = new ChunkState(this, position);
                this.states.put(position.x(), position.z(), state);
                this.executor.submit((Runnable)new EnumerateRegions(position));
            }
        }
        return state;
    }

    private ChunkState getOrCreate(BlockVector2 position) {
        return this.get(position, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rebuild() {
        Object object = this.lock;
        synchronized (object) {
            ListeningExecutorService previousExecutor = this.executor;
            LongHashTable<ChunkState> previousStates = this.states;
            previousExecutor.shutdownNow();
            this.states = new LongHashTable();
            this.executor = this.createExecutor();
            ArrayList<BlockVector2> positions = new ArrayList<BlockVector2>();
            for (ChunkState state : previousStates.values()) {
                BlockVector2 position = state.getPosition();
                positions.add(position);
                this.states.put(position.x(), position.z(), new ChunkState(this, position));
            }
            if (!positions.isEmpty()) {
                this.executor.submit((Runnable)new EnumerateRegions(positions));
            }
            this.lastState = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        ListeningExecutorService previousExecutor;
        Object object = this.lock;
        synchronized (object) {
            previousExecutor = this.executor;
            this.executor = this.createExecutor();
        }
        previousExecutor.shutdown();
        return previousExecutor.awaitTermination(timeout, unit);
    }

    @Override
    public void bias(BlockVector2 chunkPosition) {
        Preconditions.checkNotNull((Object)chunkPosition);
        this.getOrCreate(chunkPosition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void biasAll(Collection<BlockVector2> chunkPositions) {
        Object object = this.lock;
        synchronized (object) {
            for (BlockVector2 position : chunkPositions) {
                this.bias(position);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void forget(BlockVector2 chunkPosition) {
        Preconditions.checkNotNull((Object)chunkPosition);
        Object object = this.lock;
        synchronized (object) {
            this.states.remove(chunkPosition.x(), chunkPosition.z());
            ChunkState state = this.lastState;
            if (state != null && state.getPosition().x() == chunkPosition.x() && state.getPosition().z() == chunkPosition.z()) {
                this.lastState = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void forgetAll() {
        Object object = this.lock;
        synchronized (object) {
            this.executor.shutdownNow();
            this.states = new LongHashTable();
            this.executor = this.createExecutor();
            this.lastState = null;
        }
    }

    @Override
    public void add(ProtectedRegion region) {
        this.index.add(region);
        this.rebuild();
    }

    @Override
    public void addAll(Collection<ProtectedRegion> regions) {
        this.index.addAll(regions);
        this.rebuild();
    }

    @Override
    public Set<ProtectedRegion> remove(String id, RemovalStrategy strategy) {
        Set<ProtectedRegion> removed = this.index.remove(id, strategy);
        this.rebuild();
        return removed;
    }

    @Override
    public boolean contains(String id) {
        return this.index.contains(id);
    }

    @Override
    @Nullable
    public ProtectedRegion get(String id) {
        return this.index.get(id);
    }

    @Override
    public void apply(Predicate<ProtectedRegion> consumer) {
        this.index.apply(consumer);
    }

    @Override
    public void applyContaining(BlockVector3 position, Predicate<ProtectedRegion> consumer) {
        Preconditions.checkNotNull((Object)position);
        Preconditions.checkNotNull(consumer);
        ChunkState state = this.lastState;
        int chunkX = position.x() >> 4;
        int chunkZ = position.z() >> 4;
        if (state == null || state.getPosition().x() != chunkX || state.getPosition().z() != chunkZ) {
            state = this.get(BlockVector2.at((int)chunkX, (int)chunkZ), false);
        }
        if (state != null && state.isLoaded()) {
            for (ProtectedRegion region : state.getRegions()) {
                if (!region.contains(position)) continue;
                consumer.test(region);
            }
        } else {
            this.index.applyContaining(position, consumer);
        }
    }

    @Override
    public void applyIntersecting(ProtectedRegion region, Predicate<ProtectedRegion> consumer) {
        this.index.applyIntersecting(region, consumer);
    }

    @Override
    public int size() {
        return this.index.size();
    }

    @Override
    public RegionDifference getAndClearDifference() {
        return this.index.getAndClearDifference();
    }

    @Override
    public void setDirty(RegionDifference difference) {
        this.index.setDirty(difference);
    }

    @Override
    public Collection<ProtectedRegion> values() {
        return this.index.values();
    }

    @Override
    public boolean isDirty() {
        return this.index.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.index.setDirty(dirty);
    }

    private class ChunkState {
        private final BlockVector2 position;
        private boolean loaded = false;
        private List<ProtectedRegion> regions = Collections.emptyList();

        private ChunkState(ChunkHashTable chunkHashTable, BlockVector2 position) {
            this.position = position;
        }

        public BlockVector2 getPosition() {
            return this.position;
        }

        public List<ProtectedRegion> getRegions() {
            return this.regions;
        }

        public void setRegions(List<ProtectedRegion> regions) {
            this.regions = regions;
            this.loaded = true;
        }

        public boolean isLoaded() {
            return this.loaded;
        }
    }

    private class EnumerateRegions
    implements Runnable {
        private final List<BlockVector2> positions;

        private EnumerateRegions(BlockVector2 position) {
            this(Arrays.asList((BlockVector2)Preconditions.checkNotNull((Object)position)));
        }

        private EnumerateRegions(List<BlockVector2> positions) {
            Preconditions.checkNotNull(positions);
            Preconditions.checkArgument((!positions.isEmpty() ? 1 : 0) != 0, (Object)"List of positions can't be empty");
            this.positions = positions;
        }

        @Override
        public void run() {
            for (BlockVector2 position : this.positions) {
                ChunkState state = ChunkHashTable.this.get(position, false);
                if (state == null) continue;
                ArrayList regions = new ArrayList();
                ProtectedCuboidRegion chunkRegion = new ProtectedCuboidRegion("_", position.multiply(16).toBlockVector3(Integer.MIN_VALUE), position.add(1, 1).multiply(16).toBlockVector3(Integer.MAX_VALUE));
                ChunkHashTable.this.index.applyIntersecting(chunkRegion, (Predicate<ProtectedRegion>)((Object)new RegionCollectionConsumer(regions, false)));
                Collections.sort(regions);
                state.setRegions(Collections.unmodifiableList(regions));
                if (!Thread.currentThread().isInterrupted()) continue;
                return;
            }
        }
    }

    public static class Factory
    implements Function<String, ChunkHashTable> {
        private final Function<String, ? extends ConcurrentRegionIndex> supplier;

        public Factory(Function<String, ? extends ConcurrentRegionIndex> supplier) {
            Preconditions.checkNotNull(supplier);
            this.supplier = supplier;
        }

        @Override
        public ChunkHashTable apply(String name) {
            return new ChunkHashTable(this.supplier.apply(name), name);
        }
    }
}

