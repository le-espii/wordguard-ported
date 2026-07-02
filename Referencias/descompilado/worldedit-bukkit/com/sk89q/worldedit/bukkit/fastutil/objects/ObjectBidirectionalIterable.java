/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.objects;

import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectBidirectionalIterator;
import com.sk89q.worldedit.bukkit.fastutil.objects.ObjectIterable;

public interface ObjectBidirectionalIterable<K>
extends ObjectIterable<K> {
    @Override
    public ObjectBidirectionalIterator<K> iterator();
}

