/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.util;

import java.util.Optional;
import javax.annotation.Nullable;

public interface ValueProvider<C, T> {
    public static <C, T> ValueProvider<C, T> constant(@Nullable T value) {
        Optional opt = Optional.ofNullable(value);
        return context -> opt;
    }

    public Optional<T> value(C var1);
}

