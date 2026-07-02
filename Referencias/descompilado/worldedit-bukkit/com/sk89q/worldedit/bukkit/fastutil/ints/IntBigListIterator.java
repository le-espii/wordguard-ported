/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.ints;

import com.sk89q.worldedit.bukkit.fastutil.BigListIterator;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.ints.IntBidirectionalIterator;

public interface IntBigListIterator
extends IntBidirectionalIterator,
BigListIterator<Integer> {
    @Override
    default public void set(int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public void set(Integer k) {
        this.set((int)k);
    }

    @Override
    @Deprecated
    default public void add(Integer k) {
        this.add((int)k);
    }

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.nextInt();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previousInt();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

