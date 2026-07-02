/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap
 *  it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap$BasicEntry
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2IntMaps
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.AbstractObjectSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nonnull
 */
package com.sk89q.worldedit.util.collection;

import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

class Int2BaseBlockMap
extends AbstractInt2ObjectMap<BaseBlock> {
    private final Int2IntMap commonMap = new Int2IntOpenHashMap(64, 0.9f);
    private final Int2ObjectMap<BaseBlock> uncommonMap = new Int2ObjectOpenHashMap(1, 0.75f);

    Int2BaseBlockMap() {
        this.commonMap.defaultReturnValue(BlockStateIdAccess.invalidId());
    }

    private static int optimizedInternalId(BaseBlock block) {
        if (block.getNbtReference() != null) {
            return BlockStateIdAccess.invalidId();
        }
        return BlockStateIdAccess.getBlockStateId(block.toImmutableState());
    }

    private static BaseBlock assumeAsBlock(int id) {
        if (!BlockStateIdAccess.isValidInternalId(id)) {
            return null;
        }
        BlockState state = BlockStateIdAccess.getBlockStateById(id);
        if (state == null) {
            throw new IllegalStateException("No state for ID " + id);
        }
        return state.toBaseBlock();
    }

    public int size() {
        return this.commonMap.size() + this.uncommonMap.size();
    }

    public ObjectSet<Int2ObjectMap.Entry<BaseBlock>> int2ObjectEntrySet() {
        return new AbstractObjectSet<Int2ObjectMap.Entry<BaseBlock>>(){

            @Nonnull
            public ObjectIterator<Int2ObjectMap.Entry<BaseBlock>> iterator() {
                return new ObjectIterator<Int2ObjectMap.Entry<BaseBlock>>(){
                    private final ObjectIterator<Int2IntMap.Entry> commonIter;
                    private final ObjectIterator<Int2ObjectMap.Entry<BaseBlock>> uncommonIter;
                    private boolean lastNextFromCommon;
                    {
                        this.commonIter = Int2IntMaps.fastIterator((Int2IntMap)Int2BaseBlockMap.this.commonMap);
                        this.uncommonIter = Int2ObjectMaps.fastIterator(Int2BaseBlockMap.this.uncommonMap);
                        this.lastNextFromCommon = false;
                    }

                    public boolean hasNext() {
                        return this.commonIter.hasNext() || this.uncommonIter.hasNext();
                    }

                    public Int2ObjectMap.Entry<BaseBlock> next() {
                        if (this.commonIter.hasNext()) {
                            Int2IntMap.Entry e = (Int2IntMap.Entry)this.commonIter.next();
                            this.lastNextFromCommon = true;
                            return new AbstractInt2ObjectMap.BasicEntry(e.getIntKey(), (Object)Int2BaseBlockMap.assumeAsBlock(e.getIntValue()));
                        }
                        if (this.uncommonIter.hasNext()) {
                            this.lastNextFromCommon = false;
                            return (Int2ObjectMap.Entry)this.uncommonIter.next();
                        }
                        throw new NoSuchElementException();
                    }

                    public void remove() {
                        if (this.lastNextFromCommon) {
                            this.commonIter.remove();
                        } else {
                            this.uncommonIter.remove();
                        }
                    }
                };
            }

            public int size() {
                return Int2BaseBlockMap.this.size();
            }
        };
    }

    public BaseBlock get(int key) {
        int oldId = this.commonMap.get(key);
        if (!BlockStateIdAccess.isValidInternalId(oldId)) {
            return (BaseBlock)this.uncommonMap.get(key);
        }
        return Int2BaseBlockMap.assumeAsBlock(oldId);
    }

    public boolean containsKey(int k) {
        return this.commonMap.containsKey(k) || this.uncommonMap.containsKey(k);
    }

    public boolean containsValue(Object v) {
        BaseBlock block = (BaseBlock)v;
        int internalId = Int2BaseBlockMap.optimizedInternalId(block);
        if (!BlockStateIdAccess.isValidInternalId(internalId)) {
            return this.uncommonMap.containsValue((Object)block);
        }
        return this.commonMap.containsValue(internalId);
    }

    public BaseBlock put(int key, BaseBlock value) {
        int internalId = Int2BaseBlockMap.optimizedInternalId(value);
        if (!BlockStateIdAccess.isValidInternalId(internalId)) {
            BaseBlock old = (BaseBlock)this.uncommonMap.put(key, (Object)value);
            if (old == null) {
                int oldId = this.commonMap.remove(key);
                return Int2BaseBlockMap.assumeAsBlock(oldId);
            }
            return old;
        }
        int oldId = this.commonMap.put(key, internalId);
        return BlockStateIdAccess.isValidInternalId(oldId) ? Int2BaseBlockMap.assumeAsBlock(oldId) : (BaseBlock)this.uncommonMap.remove(key);
    }

    public BaseBlock remove(int key) {
        int removed = this.commonMap.remove(key);
        if (!BlockStateIdAccess.isValidInternalId(removed)) {
            return (BaseBlock)this.uncommonMap.remove(key);
        }
        return Int2BaseBlockMap.assumeAsBlock(removed);
    }

    public void replaceAll(BiFunction<? super Integer, ? super BaseBlock, ? extends BaseBlock> function) {
        int internalId;
        BaseBlock value;
        Int2IntMap.Entry next;
        ObjectIterator iter = Int2IntMaps.fastIterator((Int2IntMap)this.commonMap);
        while (iter.hasNext()) {
            next = (Int2IntMap.Entry)iter.next();
            value = function.apply((Integer)next.getIntKey(), Int2BaseBlockMap.assumeAsBlock(next.getIntValue()));
            internalId = Int2BaseBlockMap.optimizedInternalId(value);
            if (!BlockStateIdAccess.isValidInternalId(internalId)) {
                this.uncommonMap.put(next.getIntKey(), (Object)value);
                iter.remove();
                continue;
            }
            next.setValue(internalId);
        }
        iter = Int2ObjectMaps.fastIterator(this.uncommonMap);
        while (iter.hasNext()) {
            next = (Int2ObjectMap.Entry)iter.next();
            value = function.apply((Integer)next.getIntKey(), (BaseBlock)next.getValue());
            internalId = Int2BaseBlockMap.optimizedInternalId(value);
            if (!BlockStateIdAccess.isValidInternalId(internalId)) {
                next.setValue((Object)value);
                continue;
            }
            this.commonMap.put(next.getIntKey(), internalId);
            iter.remove();
        }
    }
}

