/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.inject;

import java.util.Optional;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.InjectedValueStore;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.util.ValueProvider;

public class ForwardingValueStore
implements InjectedValueStore {
    protected final InjectedValueStore delegate;

    public ForwardingValueStore(InjectedValueStore delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> void injectValue(Key<T> key, ValueProvider<InjectedValueAccess, T> provider) {
        this.delegate.injectValue(key, provider);
    }

    @Override
    public <T> Optional<T> injectedValue(Key<T> key, InjectedValueAccess context) {
        return this.delegate.injectedValue(key, context);
    }
}

