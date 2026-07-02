/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.shorts;

import com.sk89q.worldedit.bukkit.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ShortConsumer
extends Consumer<Short>,
IntConsumer {
    @Override
    public void accept(short var1);

    @Override
    @Deprecated
    default public void accept(int t) {
        this.accept(SafeMath.safeIntToShort(t));
    }

    @Override
    @Deprecated
    default public void accept(Short t) {
        this.accept((short)t);
    }

    default public ShortConsumer andThen(ShortConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Override
    default public ShortConsumer andThen(IntConsumer after) {
        return this.andThen(after instanceof ShortConsumer ? (ShortConsumer)after : after::accept);
    }

    @Override
    @Deprecated
    default public Consumer<Short> andThen(Consumer<? super Short> after) {
        return Consumer.super.andThen(after);
    }
}

