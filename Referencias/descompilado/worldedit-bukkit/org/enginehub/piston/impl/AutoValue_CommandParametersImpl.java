/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package org.enginehub.piston.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.enginehub.piston.CommandMetadata;
import org.enginehub.piston.CommandValue;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.impl.CommandParametersImpl;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.part.CommandPart;

final class AutoValue_CommandParametersImpl
extends CommandParametersImpl {
    private final ImmutableSet<CommandPart> presentParts;
    private final ImmutableMap<CommandPart, CommandValue> values;
    private final InjectedValueAccess injectedValues;
    private final CommandMetadata metadata;
    private final ArgumentConverterAccess converters;

    private AutoValue_CommandParametersImpl(ImmutableSet<CommandPart> presentParts, ImmutableMap<CommandPart, CommandValue> values, InjectedValueAccess injectedValues, CommandMetadata metadata, ArgumentConverterAccess converters) {
        this.presentParts = presentParts;
        this.values = values;
        this.injectedValues = injectedValues;
        this.metadata = metadata;
        this.converters = converters;
    }

    @Override
    ImmutableSet<CommandPart> presentParts() {
        return this.presentParts;
    }

    @Override
    ImmutableMap<CommandPart, CommandValue> values() {
        return this.values;
    }

    @Override
    InjectedValueAccess injectedValues() {
        return this.injectedValues;
    }

    @Override
    CommandMetadata metadata() {
        return this.metadata;
    }

    @Override
    ArgumentConverterAccess converters() {
        return this.converters;
    }

    public String toString() {
        return "CommandParametersImpl{presentParts=" + this.presentParts + ", values=" + this.values + ", injectedValues=" + this.injectedValues + ", metadata=" + this.metadata + ", converters=" + this.converters + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CommandParametersImpl) {
            CommandParametersImpl that = (CommandParametersImpl)o;
            return this.presentParts.equals(that.presentParts()) && this.values.equals(that.values()) && this.injectedValues.equals(that.injectedValues()) && this.metadata.equals(that.metadata()) && this.converters.equals(that.converters());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.presentParts.hashCode();
        h$ *= 1000003;
        h$ ^= this.values.hashCode();
        h$ *= 1000003;
        h$ ^= this.injectedValues.hashCode();
        h$ *= 1000003;
        h$ ^= this.metadata.hashCode();
        h$ *= 1000003;
        return h$ ^= this.converters.hashCode();
    }

    static final class Builder
    implements CommandParametersImpl.Builder {
        private ImmutableSet.Builder<CommandPart> presentPartsBuilder$;
        private ImmutableSet<CommandPart> presentParts;
        private ImmutableMap.Builder<CommandPart, CommandValue> valuesBuilder$;
        private ImmutableMap<CommandPart, CommandValue> values;
        private InjectedValueAccess injectedValues;
        private CommandMetadata metadata;
        private ArgumentConverterAccess converters;

        Builder() {
        }

        @Override
        public ImmutableSet.Builder<CommandPart> presentPartsBuilder() {
            if (this.presentPartsBuilder$ == null) {
                this.presentPartsBuilder$ = ImmutableSet.builder();
            }
            return this.presentPartsBuilder$;
        }

        @Override
        public ImmutableMap.Builder<CommandPart, CommandValue> valuesBuilder() {
            if (this.valuesBuilder$ == null) {
                this.valuesBuilder$ = ImmutableMap.builder();
            }
            return this.valuesBuilder$;
        }

        @Override
        public CommandParametersImpl.Builder injectedValues(InjectedValueAccess injectedValues) {
            if (injectedValues == null) {
                throw new NullPointerException("Null injectedValues");
            }
            this.injectedValues = injectedValues;
            return this;
        }

        @Override
        public CommandParametersImpl.Builder metadata(CommandMetadata metadata) {
            if (metadata == null) {
                throw new NullPointerException("Null metadata");
            }
            this.metadata = metadata;
            return this;
        }

        @Override
        public CommandParametersImpl.Builder converters(ArgumentConverterAccess converters) {
            if (converters == null) {
                throw new NullPointerException("Null converters");
            }
            this.converters = converters;
            return this;
        }

        @Override
        public CommandParametersImpl build() {
            if (this.presentPartsBuilder$ != null) {
                this.presentParts = this.presentPartsBuilder$.build();
            } else if (this.presentParts == null) {
                this.presentParts = ImmutableSet.of();
            }
            if (this.valuesBuilder$ != null) {
                this.values = this.valuesBuilder$.buildOrThrow();
            } else if (this.values == null) {
                this.values = ImmutableMap.of();
            }
            if (this.injectedValues == null || this.metadata == null || this.converters == null) {
                StringBuilder missing = new StringBuilder();
                if (this.injectedValues == null) {
                    missing.append(" injectedValues");
                }
                if (this.metadata == null) {
                    missing.append(" metadata");
                }
                if (this.converters == null) {
                    missing.append(" converters");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_CommandParametersImpl(this.presentParts, this.values, this.injectedValues, this.metadata, this.converters);
        }
    }
}

