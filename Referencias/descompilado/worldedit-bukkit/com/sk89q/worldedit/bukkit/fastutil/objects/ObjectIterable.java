/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectIterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectSpliterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectSpliterators;

public interface ObjectIterable<K>
extends Iterable<K> {
    @Override
    public ObjectIterator<K> iterator();

    @Override
    default public ObjectSpliterator<K> spliterator() {
        return ObjectSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }
}

