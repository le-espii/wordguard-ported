/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.util;

import java.util.stream.Stream;

public final class StreamHelper {
    public static <T> Stream<T> cast(Stream<?> stream, Class<T> type) {
        return stream.filter(type::isInstance).map(type::cast);
    }

    private StreamHelper() {
    }
}

