/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.function;

import java.io.IOException;
import java.io.UncheckedIOException;

@FunctionalInterface
public interface IORunnable {
    public static Runnable unchecked(IORunnable runnable) {
        return () -> {
            try {
                runnable.run();
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public void run() throws IOException;
}

