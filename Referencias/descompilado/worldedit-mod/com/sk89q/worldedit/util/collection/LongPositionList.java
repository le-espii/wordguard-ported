/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongBidirectionalIterator
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.longs.LongListIterator
 */
package com.sk89q.worldedit.util.collection;

import com.google.common.collect.AbstractIterator;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.PositionList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

class LongPositionList
implements PositionList {
    private final LongList delegate = new LongArrayList();

    LongPositionList() {
    }

    @Override
    public BlockVector3 get(int index) {
        return BlockVector3.fromLongPackedForm(this.delegate.getLong(index));
    }

    @Override
    public void add(BlockVector3 vector) {
        this.delegate.add(vector.toLongPackedForm());
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return new PositionIterator(this.delegate.iterator(), ListIterator::hasNext, LongIterator::nextLong);
    }

    @Override
    public Iterator<BlockVector3> reverseIterator() {
        return new PositionIterator(this.delegate.listIterator(this.size()), ListIterator::hasPrevious, LongBidirectionalIterator::previousLong);
    }

    private static final class PositionIterator
    extends AbstractIterator<BlockVector3> {
        private final LongListIterator iterator;
        private final Predicate<LongListIterator> hasNext;
        private final ToLongFunction<LongListIterator> next;

        private PositionIterator(LongListIterator iterator, Predicate<LongListIterator> hasNext, ToLongFunction<LongListIterator> next) {
            this.iterator = iterator;
            this.hasNext = hasNext;
            this.next = next;
        }

        protected BlockVector3 computeNext() {
            return this.hasNext.test(this.iterator) ? BlockVector3.fromLongPackedForm(this.next.applyAsLong(this.iterator)) : (BlockVector3)((Object)this.endOfData());
        }
    }
}

