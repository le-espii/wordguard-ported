/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.Size64;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectBidirectionalIterable;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectBidirectionalIterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectSet;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectSpliterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectSpliterators;
import java.util.SortedSet;

public interface ObjectSortedSet<K>
extends ObjectSet<K>,
SortedSet<K>,
ObjectBidirectionalIterable<K> {
    public ObjectBidirectionalIterator<K> iterator(K var1);

    @Override
    public ObjectBidirectionalIterator<K> iterator();

    @Override
    default public ObjectSpliterator<K> spliterator() {
        return ObjectSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 85, this.comparator());
    }

    @Override
    public ObjectSortedSet<K> subSet(K var1, K var2);

    @Override
    public ObjectSortedSet<K> headSet(K var1);

    @Override
    public ObjectSortedSet<K> tailSet(K var1);
}

