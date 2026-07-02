/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.function;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

@FunctionalInterface
public interface IOFunction<T, R> {
    public static <T, R> Function<T, R> unchecked(IOFunction<T, R> function) {
        return param -> {
            try {
                return function.apply(param);
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public R apply(T var1) throws IOException;
}

