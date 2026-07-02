/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.inject;

import java.util.Optional;
import org.enginehub.piston.inject.EmptyInjectedValueAccess;
import org.enginehub.piston.inject.Key;

public interface InjectedValueAccess {
    public static final InjectedValueAccess EMPTY = EmptyInjectedValueAccess.INSTANCE;

    default public <T> Optional<T> injectedValue(Key<T> key) {
        return this.injectedValue(key, this);
    }

    public <T> Optional<T> injectedValue(Key<T> var1, InjectedValueAccess var2);
}

