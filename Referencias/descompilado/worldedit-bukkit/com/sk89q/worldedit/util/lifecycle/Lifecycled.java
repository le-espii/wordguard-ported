/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.lifecycle;

import com.sk89q.worldedit.util.lifecycle.FlatMapLifecycled;
import com.sk89q.worldedit.util.lifecycle.MapLifecycled;
import com.sk89q.worldedit.util.lifecycle.SimpleLifecycled;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Lifecycled<T> {
    public Optional<T> value();

    default public T valueOrThrow() throws IllegalStateException {
        return this.value().orElseThrow(() -> new IllegalStateException("Currently invalid"));
    }

    default public boolean isValid() {
        return this.value().isPresent();
    }

    public Events<T> events();

    default public <U> Lifecycled<U> map(Function<T, U> mapper) {
        return new MapLifecycled<T, U>(this, mapper);
    }

    default public Lifecycled<T> filter(Predicate<T> filterer) {
        SimpleLifecycled downstream = SimpleLifecycled.invalid();
        this.events().onInvalidated(downstream, (d, lifecycled) -> d.invalidate());
        this.events().onNewValue(downstream, (d, lifecycled) -> {
            Object value = lifecycled.valueOrThrow();
            if (filterer.test(value)) {
                d.newValue(value);
            }
        });
        return downstream;
    }

    default public <U> Lifecycled<U> flatMap(Function<T, Lifecycled<U>> mapper) {
        return new FlatMapLifecycled<T, U>(this, mapper);
    }

    public static interface Events<T> {
        public <O> void onNewValue(O var1, BiConsumer<O, ? super Lifecycled<T>> var2);

        public <O> void onInvalidated(O var1, BiConsumer<O, ? super Lifecycled<T>> var2);
    }
}

