/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.enginehub.piston;

import javax.annotation.Nullable;
import org.enginehub.piston.CommandMetadata;
import org.enginehub.piston.NoInputCommandParameters;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.inject.InjectedValueAccess;

final class AutoValue_NoInputCommandParameters
extends NoInputCommandParameters {
    private final InjectedValueAccess injectedValues;
    @Nullable
    private final CommandMetadata metadata;
    private final ArgumentConverterAccess converters;

    private AutoValue_NoInputCommandParameters(InjectedValueAccess injectedValues, @Nullable CommandMetadata metadata, ArgumentConverterAccess converters) {
        this.injectedValues = injectedValues;
        this.metadata = metadata;
        this.converters = converters;
    }

    @Override
    InjectedValueAccess injectedValues() {
        return this.injectedValues;
    }

    @Override
    @Nullable
    CommandMetadata metadata() {
        return this.metadata;
    }

    @Override
    ArgumentConverterAccess converters() {
        return this.converters;
    }

    public String toString() {
        return "NoInputCommandParameters{injectedValues=" + this.injectedValues + ", metadata=" + this.metadata + ", converters=" + this.converters + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof NoInputCommandParameters) {
            NoInputCommandParameters that = (NoInputCommandParameters)o;
            return this.injectedValues.equals(that.injectedValues()) && (this.metadata == null ? that.metadata() == null : this.metadata.equals(that.metadata())) && this.converters.equals(that.converters());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.injectedValues.hashCode();
        h$ *= 1000003;
        h$ ^= this.metadata == null ? 0 : this.metadata.hashCode();
        h$ *= 1000003;
        return h$ ^= this.converters.hashCode();
    }

    static final class Builder
    implements NoInputCommandParameters.Builder {
        private InjectedValueAccess injectedValues;
        private CommandMetadata metadata;
        private ArgumentConverterAccess converters;

        Builder() {
        }

        @Override
        public NoInputCommandParameters.Builder injectedValues(InjectedValueAccess injectedValues) {
            if (injectedValues == null) {
                throw new NullPointerException("Null injectedValues");
            }
            this.injectedValues = injectedValues;
            return this;
        }

        @Override
        public NoInputCommandParameters.Builder metadata(@Nullable CommandMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        @Override
        public NoInputCommandParameters.Builder converters(ArgumentConverterAccess converters) {
            if (converters == null) {
                throw new NullPointerException("Null converters");
            }
            this.converters = converters;
            return this;
        }

        @Override
        public NoInputCommandParameters build() {
            if (this.injectedValues == null || this.converters == null) {
                StringBuilder missing = new StringBuilder();
                if (this.injectedValues == null) {
                    missing.append(" injectedValues");
                }
                if (this.converters == null) {
                    missing.append(" converters");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_NoInputCommandParameters(this.injectedValues, this.metadata, this.converters);
        }
    }
}

