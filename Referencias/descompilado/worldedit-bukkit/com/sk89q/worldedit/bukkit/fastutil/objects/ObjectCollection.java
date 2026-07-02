/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.Size64;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectIterable;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectIterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectSpliterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectSpliterators;
import java.util.Collection;

public interface ObjectCollection<K>
extends Collection<K>,
ObjectIterable<K> {
    @Override
    public ObjectIterator<K> iterator();

    @Override
    default public ObjectSpliterator<K> spliterator() {
        return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 64);
    }
}

