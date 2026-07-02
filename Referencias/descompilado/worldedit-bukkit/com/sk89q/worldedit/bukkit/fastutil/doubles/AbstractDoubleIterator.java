/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.doubles;

import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleConsumer;
import com.sk89q.worldedit.bukkit.fastutil.doubles.DoubleIterator;

public abstract class AbstractDoubleIterator
implements DoubleIterator {
    protected AbstractDoubleIterator() {
    }

    @Override
    public final void forEachRemaining(DoubleConsumer action) {
        this.forEachRemaining((java.util.function.DoubleConsumer)action);
    }
}

