/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.ints;

import com.sk89q.worldedit.bukkit.fastutil.ints.IntIterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectBidirectionalIterator;

public interface IntBidirectionalIterator
extends IntIterator,
ObjectBidirectionalIterator<Integer> {
    public int previousInt();

    @Override
    @Deprecated
    default public Integer previous() {
        return this.previousInt();
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousInt();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return IntIterator.super.skip(n);
    }
}

