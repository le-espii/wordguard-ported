/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.objects.AbstractObjectSet;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectBidirectionalIterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectSortedSet;

public abstract class AbstractObjectSortedSet<K>
extends AbstractObjectSet<K>
implements ObjectSortedSet<K> {
    protected AbstractObjectSortedSet() {
    }

    @Override
    public abstract ObjectBidirectionalIterator<K> iterator();
}

