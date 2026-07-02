/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil;

import java.util.Iterator;

public interface BidirectionalIterator<K>
extends Iterator<K> {
    public K previous();

    public boolean hasPrevious();
}

