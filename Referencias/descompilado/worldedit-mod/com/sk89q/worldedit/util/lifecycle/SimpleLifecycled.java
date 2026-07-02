/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.lifecycle;

import com.sk89q.worldedit.util.lifecycle.Lifecycled;
import com.sk89q.worldedit.util.lifecycle.LifecycledCallbackHandler;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public final class SimpleLifecycled<T>
implements Lifecycled<T> {
    private final LifecycledCallbackHandler<T> events = new LifecycledCallbackHandler(this);
    @Nullable
    private T value;

    public static <T> SimpleLifecycled<T> valid(T value) {
        return new SimpleLifecycled<T>(Objects.requireNonNull(value));
    }

    public static <T> SimpleLifecycled<T> invalid() {
        return new SimpleLifecycled<Object>(null);
    }

    private SimpleLifecycled(@Nullable T value) {
        this.value = value;
    }

    public void newValue(T value) {
        this.invalidate();
        this.value = Objects.requireNonNull(value);
        this.events.fireOnNewValue();
    }

    public void invalidate() {
        boolean fire = this.value != null;
        this.value = null;
        if (fire) {
            this.events.fireInvalidated();
        }
    }

    @Override
    public Optional<T> value() {
        return Optional.ofNullable(this.value);
    }

    @Override
    public Lifecycled.Events<T> events() {
        return this.events;
    }
}

