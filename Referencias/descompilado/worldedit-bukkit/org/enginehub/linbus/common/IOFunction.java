/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.common;

import java.io.IOException;

public interface IOFunction<T, R> {
    public R apply(T var1) throws IOException;
}

