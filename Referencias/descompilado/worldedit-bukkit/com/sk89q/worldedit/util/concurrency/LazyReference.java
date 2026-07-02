/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public final class LazyReference<T> {
    private Object value;

    public static <T> LazyReference<T> from(Supplier<T> valueComputation) {
        return new LazyReference<Supplier<T>>(valueComputation);
    }

    public static <T> LazyReference<T> computed(T value) {
        return new LazyReference<T>(value);
    }

    private LazyReference(Supplier<T> valueComputation) {
        this.value = new RefInfo<T>(valueComputation);
    }

    private LazyReference(T value) {
        this.value = value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getValue() {
        Object v = this.value;
        if (!(v instanceof RefInfo)) {
            return (T)v;
        }
        RefInfo refInfo = (RefInfo)v;
        refInfo.lock.lock();
        try {
            v = this.value;
            if (!(v instanceof RefInfo)) {
                Object object = v;
                return (T)object;
            }
            this.value = v = refInfo.valueComputation.get();
            Object object = v;
            return (T)object;
        }
        finally {
            refInfo.lock.unlock();
        }
    }

    private static final class RefInfo<T> {
        private final Lock lock = new ReentrantLock();
        private final Supplier<T> valueComputation;

        private RefInfo(Supplier<T> valueComputation) {
            this.valueComputation = valueComputation;
        }
    }
}

