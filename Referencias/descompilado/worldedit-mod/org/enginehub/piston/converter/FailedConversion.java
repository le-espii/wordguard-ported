/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package org.enginehub.piston.converter;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversionMapper;

public final class FailedConversion<T>
extends ConversionResult<T> {
    private final Throwable error;
    private final ImmutableSet<FailedConversion<T>> otherFailures;

    public static <T> FailedConversion<T> from(Throwable error) {
        return FailedConversion.from(error, ImmutableSet.of());
    }

    public static <T> FailedConversion<T> from(Throwable error, Collection<FailedConversion<T>> otherFailures) {
        return new FailedConversion<T>(error, otherFailures);
    }

    public FailedConversion(Throwable error, Collection<FailedConversion<T>> otherFailures) {
        this.error = error;
        this.otherFailures = ImmutableSet.copyOf(otherFailures);
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public <U> ConversionResult<U> failureAsAny() {
        return this;
    }

    @Override
    public ConversionResult<T> orElse(ConversionResult<T> result) {
        return result.isSuccessful() ? result : FailedConversion.from(this.error, ImmutableSet.builder().addAll(this.otherFailures).add((Object)((FailedConversion)result)).build());
    }

    @Override
    public <U> ConversionResult<U> map(Function<? super Collection<T>, ? extends Collection<U>> mapper) {
        return this.failureAsAny();
    }

    public Throwable getError() {
        return this.error;
    }

    public ImmutableSet<FailedConversion<T>> getOtherFailures() {
        return this.otherFailures;
    }

    @Override
    public Collection<T> get() {
        throw FailedConversionMapper.mapOnto(NoSuchElementException::new, this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FailedConversion that = (FailedConversion)o;
        return this.error.equals(that.error) && this.otherFailures.equals(that.otherFailures);
    }

    public int hashCode() {
        return Objects.hash(this.error, this.otherFailures);
    }
}

