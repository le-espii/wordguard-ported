/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.BigListIterator;
import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectBidirectionalIterator;

public interface ObjectBigListIterator<K>
extends ObjectBidirectionalIterator<K>,
BigListIterator<K> {
    @Override
    default public void set(K k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(K k) {
        throw new UnsupportedOperationException();
    }

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.next();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previous();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

