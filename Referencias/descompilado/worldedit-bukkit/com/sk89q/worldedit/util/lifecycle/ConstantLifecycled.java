/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.lifecycle;

import com.sk89q.worldedit.util.lifecycle.Lifecycled;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class ConstantLifecycled<T>
implements Lifecycled<T> {
    private final T value;

    public ConstantLifecycled(T value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<T> value() {
        return Optional.of(this.value);
    }

    @Override
    public Lifecycled.Events<T> events() {
        return new Lifecycled.Events<T>(){

            @Override
            public <O> void onNewValue(O owner, BiConsumer<O, ? super Lifecycled<T>> callback) {
                callback.accept(owner, ConstantLifecycled.this);
            }

            @Override
            public <O> void onInvalidated(O owner, BiConsumer<O, ? super Lifecycled<T>> callback) {
            }
        };
    }
}

