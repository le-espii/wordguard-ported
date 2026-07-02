/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.longs;

import com.sk89q.worldedit.bukkit.fastutil.longs.LongConsumer;
import com.sk89q.worldedit.bukkit.fastutil.longs.LongIterator;
import com.sk89q.worldedit.bukkit.fastutil.longs.LongSpliterator;
import com.sk89q.worldedit.bukkit.fastutil.longs.LongSpliterators;
import java.util.Objects;
import java.util.function.Consumer;

public interface LongIterable
extends Iterable<Long> {
    public LongIterator iterator();

    default public LongIterator longIterator() {
        return this.iterator();
    }

    default public LongSpliterator spliterator() {
        return LongSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public LongSpliterator longSpliterator() {
        return this.spliterator();
    }

    default public void forEach(java.util.function.LongConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    default public void forEach(LongConsumer action) {
        this.forEach((java.util.function.LongConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Long> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)((Object)action) : action::accept);
    }
}

