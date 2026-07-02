/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.doubles;

import com.sk89q.worldedit.bukkit.fastutil.Size64;
import com.sk89q.worldedit.bukkit.fastutil.doubles.AbstractDoubleSet;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleArraySet;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleCollection;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleIterator;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleOpenHashSet;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleSets;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleSpliterator;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleSpliterators;
import java.util.Set;

public interface DoubleSet
extends DoubleCollection,
Set<Double> {
    @Override
    public DoubleIterator iterator();

    @Override
    default public DoubleSpliterator spliterator() {
        return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 321);
    }

    public boolean remove(double var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return DoubleCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Double o) {
        return DoubleCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return DoubleCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(double k) {
        return this.remove(k);
    }

    public static DoubleSet of() {
        return DoubleSets.UNMODIFIABLE_EMPTY_SET;
    }

    public static DoubleSet of(double e) {
        return DoubleSets.singleton(e);
    }

    public static DoubleSet of(double e0, double e1) {
        DoubleArraySet innerSet = new DoubleArraySet(2);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return DoubleSets.unmodifiable(innerSet);
    }

    public static DoubleSet of(double e0, double e1, double e2) {
        DoubleArraySet innerSet = new DoubleArraySet(3);
        innerSet.add(e0);
        if (!innerSet.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!innerSet.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return DoubleSets.unmodifiable(innerSet);
    }

    public static DoubleSet of(double ... a) {
        switch (a.length) {
            case 0: {
                return DoubleSet.of();
            }
            case 1: {
                return DoubleSet.of(a[0]);
            }
            case 2: {
                return DoubleSet.of(a[0], a[1]);
            }
            case 3: {
                return DoubleSet.of(a[0], a[1], a[2]);
            }
        }
        AbstractDoubleSet innerSet = a.length <= 4 ? new DoubleArraySet(a.length) : new DoubleOpenHashSet(a.length);
        for (double element : a) {
            if (innerSet.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element: " + element);
        }
        return DoubleSets.unmodifiable(innerSet);
    }
}

