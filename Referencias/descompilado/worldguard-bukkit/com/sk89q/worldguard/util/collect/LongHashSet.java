/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.util.collect;

import com.sk89q.worldguard.util.collect.LongHash;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LongHashSet
extends LongHash {
    protected long[][][] values = new long[256][][];
    protected int count = 0;
    protected ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    protected ReentrantReadWriteLock.ReadLock rl = this.rwl.readLock();
    protected ReentrantReadWriteLock.WriteLock wl = this.rwl.writeLock();

    public boolean isEmpty() {
        this.rl.lock();
        try {
            boolean bl = this.count == 0;
            return bl;
        }
        finally {
            this.rl.unlock();
        }
    }

    public int size() {
        return this.count;
    }

    public void add(int msw, int lsw) {
        this.add(LongHashSet.toLong(msw, lsw));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(long key) {
        block10: {
            this.wl.lock();
            try {
                int i;
                int outerIdx;
                long[] inner;
                int mainIdx = (int)(key & 0xFFL);
                Object outer = this.values[mainIdx];
                if (outer == null) {
                    long[][] lArrayArray = new long[256][];
                    outer = lArrayArray;
                    this.values[mainIdx] = lArrayArray;
                }
                if ((inner = outer[outerIdx = (int)(key >> 32 & 0xFFL)]) == null) {
                    LongHashSet longHashSet = this;
                    synchronized (longHashSet) {
                        inner = new long[1];
                        outer[outerIdx] = inner;
                        inner[0] = key;
                        ++this.count;
                        break block10;
                    }
                }
                for (i = 0; i < inner.length; ++i) {
                    if (inner[i] != key) continue;
                    return;
                }
                inner = Arrays.copyOf(inner, i + 1);
                outer[outerIdx] = inner;
                inner[i] = key;
                ++this.count;
            }
            finally {
                this.wl.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKey(long key) {
        this.rl.lock();
        try {
            long[][] outer = this.values[(int)(key & 0xFFL)];
            if (outer == null) {
                boolean bl = false;
                return bl;
            }
            long[] inner = outer[(int)(key >> 32 & 0xFFL)];
            if (inner == null) {
                boolean bl = false;
                return bl;
            }
            for (long entry : inner) {
                if (entry != key) continue;
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.rl.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(long key) {
        this.wl.lock();
        try {
            long[][] outer = this.values[(int)(key & 0xFFL)];
            if (outer == null) {
                return;
            }
            long[] inner = outer[(int)(key >> 32 & 0xFFL)];
            if (inner == null) {
                return;
            }
            int max = inner.length - 1;
            for (int i = 0; i <= max; ++i) {
                if (inner[i] != key) continue;
                --this.count;
                if (i != max) {
                    inner[i] = inner[max];
                }
                outer[(int)(key >> 32 & 0xFFL)] = max == 0 ? null : Arrays.copyOf(inner, max);
                return;
            }
        }
        finally {
            this.wl.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long popFirst() {
        this.wl.lock();
        try {
            for (long[][] outer : this.values) {
                if (outer == null) continue;
                for (int i = 0; i < outer.length; ++i) {
                    long[] inner = outer[i];
                    if (inner == null || inner.length == 0) continue;
                    --this.count;
                    long ret = inner[inner.length - 1];
                    outer[i] = Arrays.copyOf(inner, inner.length - 1);
                    long l = ret;
                    return l;
                }
            }
        }
        finally {
            this.wl.unlock();
        }
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long[] popAll() {
        int index = 0;
        this.wl.lock();
        try {
            long[] ret = new long[this.count];
            for (long[][] outer : this.values) {
                if (outer == null) continue;
                for (int oIdx = outer.length - 1; oIdx >= 0; --oIdx) {
                    long[] inner = outer[oIdx];
                    if (inner == null) continue;
                    for (long entry : inner) {
                        ret[index++] = entry;
                    }
                    outer[oIdx] = null;
                }
            }
            this.count = 0;
            long[] lArray = ret;
            return lArray;
        }
        finally {
            this.wl.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long[] keys() {
        int index = 0;
        this.rl.lock();
        try {
            long[] ret = new long[this.count];
            for (long[][] outer : this.values) {
                if (outer == null) continue;
                for (long[] inner : outer) {
                    if (inner == null) continue;
                    for (long entry : inner) {
                        ret[index++] = entry;
                    }
                }
            }
            long[] lArray = ret;
            return lArray;
        }
        finally {
            this.rl.unlock();
        }
    }
}

