/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.ints;

import com.sk89q.worldedit.bukkit.fastutil.ints.IntConsumer;
import com.sk89q.worldedit.bukkit.fastutil.ints.IntSpliterator;

public abstract class AbstractIntSpliterator
implements IntSpliterator {
    protected AbstractIntSpliterator() {
    }

    @Override
    public final boolean tryAdvance(IntConsumer action) {
        return this.tryAdvance((java.util.function.IntConsumer)action);
    }

    @Override
    public final void forEachRemaining(IntConsumer action) {
        this.forEachRemaining((java.util.function.IntConsumer)action);
    }
}

