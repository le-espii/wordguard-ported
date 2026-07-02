/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.longs;

import com.sk89q.worldedit.bukkit.fastutil.longs.LongConsumer;
import com.sk89q.worldedit.bukkit.fastutil.longs.LongIterator;

public abstract class AbstractLongIterator
implements LongIterator {
    protected AbstractLongIterator() {
    }

    @Override
    public final void forEachRemaining(LongConsumer action) {
        this.forEachRemaining((java.util.function.LongConsumer)action);
    }
}

