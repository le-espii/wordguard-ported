/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil;

import com.sk89q.worldedit.bukkit.fastutil.BidirectionalIterator;

public interface BigListIterator<K>
extends BidirectionalIterator<K> {
    public long nextIndex();

    public long previousIndex();

    default public void set(K e) {
        throw new UnsupportedOperationException();
    }

    default public void add(K e) {
        throw new UnsupportedOperationException();
    }
}

