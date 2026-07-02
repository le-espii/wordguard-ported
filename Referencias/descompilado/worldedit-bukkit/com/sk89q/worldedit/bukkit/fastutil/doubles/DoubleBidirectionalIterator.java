/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.doubles;

import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleIterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectBidirectionalIterator;

public interface DoubleBidirectionalIterator
extends DoubleIterator,
ObjectBidirectionalIterator<Double> {
    public double previousDouble();

    @Override
    @Deprecated
    default public Double previous() {
        return this.previousDouble();
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousDouble();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return DoubleIterator.super.skip(n);
    }
}

