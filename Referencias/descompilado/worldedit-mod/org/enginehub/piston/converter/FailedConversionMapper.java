/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import java.util.function.Supplier;
import org.enginehub.piston.converter.FailedConversion;

public class FailedConversionMapper {
    public static <X extends Throwable> X mapOnto(Supplier<X> newThrowable, FailedConversion<?> conversion) {
        Throwable error = (Throwable)newThrowable.get();
        error.initCause(conversion.getError());
        conversion.getOtherFailures().forEach(f -> error.addSuppressed(f.getError()));
        return (X)error;
    }

    private FailedConversionMapper() {
    }
}

