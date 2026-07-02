/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.ints;

import com.sk89q.worldedit.bukkit.fastutil.ints.IntBidirectionalIterator;
import com.sk89q.worldedit.bukkit.fastutil.ints.IntIterable;

public interface IntBidirectionalIterable
extends IntIterable {
    @Override
    public IntBidirectionalIterator iterator();
}

