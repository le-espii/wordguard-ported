/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.doubles;

import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleBidirectionalIterator;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleIterable;

public interface DoubleBidirectionalIterable
extends DoubleIterable {
    @Override
    public DoubleBidirectionalIterator iterator();
}

