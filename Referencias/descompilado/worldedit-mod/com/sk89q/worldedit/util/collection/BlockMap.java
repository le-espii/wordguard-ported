/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 */
package com.sk89q.worldedit.util.collection;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BitMath;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.Int2BaseBlockMap;
import com.sk89q.worldedit.world.block.BaseBlock;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockMap<V>
extends AbstractMap<BlockVector3, V> {
    private static final long BITS_24 = BitMath.mask(24);
    private static final long BITS_20 = BitMath.mask(20);
    private static final int BITS_8 = BitMath.mask(8);
    private static final int BITS_6 = BitMath.mask(6);
    private static final long GROUP_X = BITS_20;
    private static final long GROUP_Z = BITS_20 << 20;
    private static final long GROUP_Y = BITS_24 << 40;
    private static final int INNER_X = BITS_6;
    private static final int INNER_Z = BITS_6 << 6;
    private static final int INNER_Y = BITS_8 << 12;
    private final Long2ObjectMap<Int2ObjectMap<V>> maps = new Long2ObjectOpenHashMap(4, 0.75f);
    private final Supplier<Int2ObjectMap<V>> subMapSupplier;
    private Set<Map.Entry<BlockVector3, V>> entrySet;
    private Collection<V> values;

    public static <V> BlockMap<V> create() {
        return BlockMap.create(() -> new Int2ObjectOpenHashMap(64, 0.9f));
    }

    public static BlockMap<BaseBlock> createForBaseBlock() {
        return BlockMap.create(Int2BaseBlockMap::new);
    }

    private static <V> BlockMap<V> create(Supplier<Int2ObjectMap<V>> subMapSupplier) {
        return new BlockMap<V>(subMapSupplier);
    }

    public static <V> BlockMap<V> copyOf(Map<? extends BlockVector3, ? extends V> source) {
        return new BlockMap<V>(Int2ObjectOpenHashMap::new, source);
    }

    private static long toGroupKey(BlockVector3 location) {
        return (long)(location.x() >>> 6) & BITS_20 | ((long)(location.z() >>> 6) & BITS_20) << 20 | ((long)(location.y() >>> 8) & BITS_24) << 40;
    }

    private static int toInnerKey(BlockVector3 location) {
        return location.x() & BITS_6 | (location.z() & BITS_6) << 6 | (location.y() & BITS_8) << 12;
    }

    private static BlockVector3 reconstructLocation(long group, int inner) {
        int groupX = (int)((group & GROUP_X) << 6);
        int x = BitMath.fixSign(groupX | inner & INNER_X, 26);
        int groupZ = (int)((group & GROUP_Z) >>> 14);
        int z = BitMath.fixSign(groupZ | (inner & INNER_Z) >>> 6, 26);
        int groupY = (int)((group & GROUP_Y) >>> 32);
        int y = groupY | (inner & INNER_Y) >>> 12;
        return BlockVector3.at(x, y, z);
    }

    private BlockMap(Supplier<Int2ObjectMap<V>> subMapSupplier) {
        this.subMapSupplier = subMapSupplier;
    }

    private BlockMap(Supplier<Int2ObjectMap<V>> subMapSupplier, Map<? extends BlockVector3, ? extends V> source) {
        this.subMapSupplier = subMapSupplier;
        this.putAll(source);
    }

    private Int2ObjectMap<V> getOrCreateMap(long groupKey) {
        return (Int2ObjectMap)this.maps.computeIfAbsent(groupKey, k -> this.subMapSupplier.get());
    }

    private Int2ObjectMap<V> getOrEmptyMap(long groupKey) {
        return (Int2ObjectMap)this.maps.getOrDefault(groupKey, (Object)Int2ObjectMaps.emptyMap());
    }

    private <R> R cleanlyModifyMap(long groupKey, Function<Int2ObjectMap<V>, R> func) {
        Int2ObjectMap<V> map = (Int2ObjectMap<V>)this.maps.get(groupKey);
        if (map != null) {
            R result = func.apply(map);
            if (map.isEmpty()) {
                this.maps.remove(groupKey);
            }
            return result;
        }
        map = this.subMapSupplier.get();
        R result = func.apply(map);
        if (!map.isEmpty()) {
            this.maps.put(groupKey, map);
        }
        return result;
    }

    @Override
    public V put(BlockVector3 key, V value) {
        return (V)this.getOrCreateMap(BlockMap.toGroupKey(key)).put(BlockMap.toInnerKey(key), value);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        BlockVector3 vec = (BlockVector3)((Object)key);
        return (V)this.getOrEmptyMap(BlockMap.toGroupKey(vec)).getOrDefault(BlockMap.toInnerKey(vec), defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super BlockVector3, ? super V> action) {
        this.maps.forEach((groupKey, m) -> m.forEach((innerKey, block) -> action.accept(BlockMap.reconstructLocation(groupKey, innerKey), (Object)block)));
    }

    @Override
    public void replaceAll(BiFunction<? super BlockVector3, ? super V, ? extends V> function) {
        this.maps.forEach((groupKey, m) -> m.replaceAll((innerKey, block) -> function.apply(BlockMap.reconstructLocation(groupKey, innerKey), (Object)block)));
    }

    @Override
    public V putIfAbsent(BlockVector3 key, V value) {
        return (V)this.getOrCreateMap(BlockMap.toGroupKey(key)).putIfAbsent(BlockMap.toInnerKey(key), value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        BlockVector3 vec = (BlockVector3)((Object)key);
        return this.cleanlyModifyMap(BlockMap.toGroupKey(vec), map -> map.remove(BlockMap.toInnerKey(vec), value));
    }

    @Override
    public boolean replace(BlockVector3 key, V oldValue, V newValue) {
        return this.cleanlyModifyMap(BlockMap.toGroupKey(key), map -> map.replace(BlockMap.toInnerKey(key), oldValue, newValue));
    }

    @Override
    public V replace(BlockVector3 key, V value) {
        return (V)this.getOrCreateMap(BlockMap.toGroupKey(key)).replace(BlockMap.toInnerKey(key), value);
    }

    @Override
    public V computeIfAbsent(BlockVector3 key, Function<? super BlockVector3, ? extends V> mappingFunction) {
        return (V)this.cleanlyModifyMap(BlockMap.toGroupKey(key), map -> map.computeIfAbsent(BlockMap.toInnerKey(key), ik -> mappingFunction.apply(key)));
    }

    @Override
    public V computeIfPresent(BlockVector3 key, BiFunction<? super BlockVector3, ? super V, ? extends V> remappingFunction) {
        return (V)this.cleanlyModifyMap(BlockMap.toGroupKey(key), map -> map.computeIfPresent(BlockMap.toInnerKey(key), (ik, block) -> remappingFunction.apply(key, (Object)block)));
    }

    @Override
    public V compute(BlockVector3 key, BiFunction<? super BlockVector3, ? super V, ? extends V> remappingFunction) {
        return (V)this.cleanlyModifyMap(BlockMap.toGroupKey(key), map -> map.compute(BlockMap.toInnerKey(key), (ik, block) -> remappingFunction.apply(key, (Object)block)));
    }

    @Override
    public V merge(BlockVector3 key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return (V)this.cleanlyModifyMap(BlockMap.toGroupKey(key), map -> map.merge(BlockMap.toInnerKey(key), value, remappingFunction));
    }

    @Override
    public Set<Map.Entry<BlockVector3, V>> entrySet() {
        AbstractSet es = this.entrySet;
        if (es == null) {
            this.entrySet = es = new AbstractSet<Map.Entry<BlockVector3, V>>(){

                @Override
                public Iterator<Map.Entry<BlockVector3, V>> iterator() {
                    return new Iterator<Map.Entry<BlockVector3, V>>(){
                        private final ObjectIterator<Long2ObjectMap.Entry<Int2ObjectMap<V>>> primaryIterator;
                        private Long2ObjectMap.Entry<Int2ObjectMap<V>> currentPrimaryEntry;
                        private ObjectIterator<Int2ObjectMap.Entry<V>> secondaryIterator;
                        private boolean finished;
                        private LazyEntry next;
                        {
                            this.primaryIterator = Long2ObjectMaps.fastIterator(BlockMap.this.maps);
                        }

                        @Override
                        public boolean hasNext() {
                            if (this.finished) {
                                return false;
                            }
                            if (this.next == null) {
                                LazyEntry proposedNext = this.computeNext();
                                if (proposedNext == null) {
                                    this.finished = true;
                                    return false;
                                }
                                this.next = proposedNext;
                            }
                            return true;
                        }

                        private LazyEntry computeNext() {
                            if (this.secondaryIterator == null || !this.secondaryIterator.hasNext()) {
                                if (!this.primaryIterator.hasNext()) {
                                    return null;
                                }
                                this.currentPrimaryEntry = (Long2ObjectMap.Entry)this.primaryIterator.next();
                                this.secondaryIterator = Int2ObjectMaps.fastIterator((Int2ObjectMap)((Int2ObjectMap)this.currentPrimaryEntry.getValue()));
                                Preconditions.checkState((boolean)this.secondaryIterator.hasNext(), (Object)"Should not have an empty map entry, it should have been removed!");
                            }
                            Int2ObjectMap.Entry next = (Int2ObjectMap.Entry)this.secondaryIterator.next();
                            return new LazyEntry(this.currentPrimaryEntry.getLongKey(), next.getIntKey(), next.getValue());
                        }

                        @Override
                        public Map.Entry<BlockVector3, V> next() {
                            if (!this.hasNext()) {
                                throw new NoSuchElementException();
                            }
                            LazyEntry tmp = this.next;
                            this.next = null;
                            return tmp;
                        }

                        @Override
                        public void remove() {
                            this.secondaryIterator.remove();
                            if (((Int2ObjectMap)this.currentPrimaryEntry.getValue()).isEmpty()) {
                                this.primaryIterator.remove();
                            }
                        }
                    };
                }

                @Override
                public int size() {
                    return BlockMap.this.size();
                }
            };
        }
        return es;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.maps.values().stream().anyMatch(m -> m.containsValue(value));
    }

    @Override
    public boolean containsKey(Object key) {
        BlockVector3 vec = (BlockVector3)((Object)key);
        Int2ObjectMap activeMap = (Int2ObjectMap)this.maps.get(BlockMap.toGroupKey(vec));
        if (activeMap == null) {
            return false;
        }
        return activeMap.containsKey(BlockMap.toInnerKey(vec));
    }

    @Override
    public V get(Object key) {
        BlockVector3 vec = (BlockVector3)((Object)key);
        Int2ObjectMap activeMap = (Int2ObjectMap)this.maps.get(BlockMap.toGroupKey(vec));
        if (activeMap == null) {
            return null;
        }
        return (V)activeMap.get(BlockMap.toInnerKey(vec));
    }

    @Override
    public V remove(Object key) {
        BlockVector3 vec = (BlockVector3)((Object)key);
        long groupKey = BlockMap.toGroupKey(vec);
        Int2ObjectMap activeMap = (Int2ObjectMap)this.maps.get(groupKey);
        if (activeMap == null) {
            return null;
        }
        Object removed = activeMap.remove(BlockMap.toInnerKey(vec));
        if (activeMap.isEmpty()) {
            this.maps.remove(groupKey);
        }
        return (V)removed;
    }

    @Override
    public void putAll(Map<? extends BlockVector3, ? extends V> m) {
        if (m instanceof BlockMap) {
            ((BlockMap)m).maps.forEach((groupKey, map) -> this.getOrCreateMap((long)groupKey).putAll((Map)map));
        } else {
            super.putAll(m);
        }
    }

    @Override
    public void clear() {
        this.maps.clear();
    }

    @Override
    public int size() {
        return this.maps.values().stream().mapToInt(Map::size).sum();
    }

    @Override
    public Collection<V> values() {
        AbstractCollection vs = this.values;
        if (vs == null) {
            this.values = vs = new AbstractCollection<V>(){

                @Override
                public Iterator<V> iterator() {
                    return BlockMap.this.maps.values().stream().flatMap(m -> m.values().stream()).iterator();
                }

                @Override
                public int size() {
                    return BlockMap.this.size();
                }
            };
        }
        return vs;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BlockMap) {
            BlockMap other = (BlockMap)o;
            return this.maps.equals(other.maps);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private final class LazyEntry
    implements Map.Entry<BlockVector3, V> {
        private final long groupKey;
        private final int innerKey;
        private BlockVector3 lazyKey;
        private V value;

        private LazyEntry(long groupKey, int innerKey, V value) {
            this.groupKey = groupKey;
            this.innerKey = innerKey;
            this.value = value;
        }

        @Override
        public BlockVector3 getKey() {
            BlockVector3 result = this.lazyKey;
            if (result == null) {
                this.lazyKey = result = BlockMap.reconstructLocation(this.groupKey, this.innerKey);
            }
            return result;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return BlockMap.this.getOrCreateMap(this.groupKey).put(this.innerKey, value);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (o instanceof LazyEntry) {
                LazyEntry otherE = (LazyEntry)o;
                return otherE.groupKey == this.groupKey && otherE.innerKey == this.innerKey && Objects.equals(this.value, e.getValue());
            }
            return Objects.equals((Object)this.getKey(), e.getKey()) && Objects.equals(this.value, e.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode((Object)this.getKey()) ^ Objects.hashCode(this.value);
        }

        public String toString() {
            return String.valueOf((Object)this.getKey()) + "=" + String.valueOf(this.getValue());
        }
    }
}

