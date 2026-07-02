/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.ints;

import com.sk89q.worldedit.bukkit.fastutil.ints.IntConsumer;
import com.sk89q.worldedit.bukkit.fastutil.ints.IntIterator;

public abstract class AbstractIntIterator
implements IntIterator {
    protected AbstractIntIterator() {
    }

    @Override
    public final void forEachRemaining(IntConsumer action) {
        this.forEachRemaining((java.util.function.IntConsumer)action);
    }
}

