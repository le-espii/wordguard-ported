/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectBidirectionalIterator;
import java.util.ListIterator;

public interface ObjectListIterator<K>
extends ObjectBidirectionalIterator<K>,
ListIterator<K> {
    @Override
    default public void set(K k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(K k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }
}

