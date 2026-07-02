/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.doubles;

import com.sk89q.worldedit.bukkit.fastutil.doubles.AbstractDoubleSet;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleBidirectionalIterator;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleSortedSet;

public abstract class AbstractDoubleSortedSet
extends AbstractDoubleSet
implements DoubleSortedSet {
    protected AbstractDoubleSortedSet() {
    }

    @Override
    public abstract DoubleBidirectionalIterator iterator();
}

