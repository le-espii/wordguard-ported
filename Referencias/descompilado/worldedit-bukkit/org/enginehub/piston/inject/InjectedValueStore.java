/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.inject;

import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.util.ValueProvider;

public interface InjectedValueStore
extends InjectedValueAccess {
    public <T> void injectValue(Key<T> var1, ValueProvider<InjectedValueAccess, T> var2);
}

