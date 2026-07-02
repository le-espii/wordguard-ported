/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.converter;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;

public final class SuccessfulConversion<T>
extends ConversionResult<T> {
    private final Collection<T> result;
    private final boolean exactMatch;

    public static <T> SuccessfulConversion<T> fromSingle(T result) {
        return SuccessfulConversion.from(ImmutableList.of(result));
    }

    public static <T> SuccessfulConversion<T> fromSingle(T result, boolean exactMatch) {
        return SuccessfulConversion.from(ImmutableList.of(result), exactMatch);
    }

    public static <T> SuccessfulConversion<T> from(Collection<T> result) {
        return new SuccessfulConversion<T>(result, true);
    }

    public static <T> SuccessfulConversion<T> from(Collection<T> result, boolean exactMatch) {
        return new SuccessfulConversion<T>(result, exactMatch);
    }

    private SuccessfulConversion(Collection<T> result, boolean exactMatch) {
        this.result = result;
        this.exactMatch = exactMatch;
    }

    public boolean isExactMatch() {
        return this.exactMatch;
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }

    @Override
    public <U> ConversionResult<U> failureAsAny() {
        throw new IllegalStateException("This is not a failed conversion result.");
    }

    @Override
    public ConversionResult<T> orElse(ConversionResult<T> result) {
        return this;
    }

    @Override
    public <U> ConversionResult<U> map(Function<? super Collection<T>, ? extends Collection<U>> mapper) {
        Collection<U> mapped;
        try {
            mapped = mapper.apply(this.get());
        }
        catch (Throwable t) {
            return FailedConversion.from(t);
        }
        if (mapped == null) {
            return FailedConversion.from(new NullPointerException());
        }
        return SuccessfulConversion.from(mapped, this.exactMatch);
    }

    @Override
    public Collection<T> get() {
        return this.result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SuccessfulConversion that = (SuccessfulConversion)o;
        return this.result.equals(that.result);
    }

    public int hashCode() {
        return Objects.hash(this.result);
    }
}

