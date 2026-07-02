/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.shorts;

import com.sk89q.worldedit.bukkit.fastutil.shorts.ShortComparator;
import com.sk89q.worldedit.bukkit.fastutil.shorts.ShortConsumer;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface ShortSpliterator
extends Spliterator.OfPrimitive<Short, ShortConsumer, ShortSpliterator> {
    @Override
    @Deprecated
    default public boolean tryAdvance(Consumer<? super Short> action) {
        return this.tryAdvance(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Short> action) {
        this.forEachRemaining(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
    }

    default public long skip(long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        long i = n;
        while (i-- != 0L && this.tryAdvance(unused -> {})) {
        }
        return n - i - 1L;
    }

    @Override
    public ShortSpliterator trySplit();

    default public ShortComparator getComparator() {
        throw new IllegalStateException();
    }
}

