/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.inject;

import java.util.Optional;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

enum EmptyInjectedValueAccess implements InjectedValueAccess
{
    INSTANCE;


    @Override
    public <T> Optional<T> injectedValue(Key<T> key, InjectedValueAccess context) {
        return Optional.empty();
    }
}

