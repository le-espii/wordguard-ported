/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package org.enginehub.piston.inject;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.inject.MapBackedValueStore;
import org.enginehub.piston.util.ValueProvider;

public final class MemoizingValueAccess
implements InjectedValueAccess {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<Key<?>, Optional<?>> memory = new HashMap();
    private final InjectedValueAccess delegate;

    public static MemoizingValueAccess wrap(InjectedValueAccess delegate) {
        if (delegate instanceof MemoizingValueAccess) {
            return (MemoizingValueAccess)delegate;
        }
        return new MemoizingValueAccess(delegate);
    }

    private MemoizingValueAccess(InjectedValueAccess delegate) {
        this.delegate = delegate;
    }

    public InjectedValueAccess snapshotMemory() {
        ImmutableMap.Builder snapshot = ImmutableMap.builder();
        this.lock.readLock().lock();
        try {
            this.memory.forEach((k, v) -> snapshot.put(k, ValueProvider.constant(v.orElse(null))));
        }
        finally {
            this.lock.readLock().unlock();
        }
        return MapBackedValueStore.create(snapshot.build());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> Optional<T> injectedValue(Key<T> key, InjectedValueAccess context) {
        Optional<Object> result;
        this.lock.readLock().lock();
        try {
            result = this.memory.get(key);
            if (result != null) {
                Optional<?> optional = result;
                return optional;
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            result = this.memory.get(key);
            if (result == null) {
                result = this.delegate.injectedValue(key, context);
                this.memory.put(key, result);
            }
            Optional<?> optional = result;
            return optional;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
}

