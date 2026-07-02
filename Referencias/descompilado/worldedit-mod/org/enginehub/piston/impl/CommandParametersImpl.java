/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package org.enginehub.piston.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.enginehub.piston.CommandMetadata;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.CommandValue;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.impl.AutoValue_CommandParametersImpl;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.ArgAcceptingCommandPart;
import org.enginehub.piston.part.CommandPart;

@AutoValue
abstract class CommandParametersImpl
implements CommandParameters {
    static Builder builder() {
        return new AutoValue_CommandParametersImpl.Builder();
    }

    CommandParametersImpl() {
    }

    abstract ImmutableSet<CommandPart> presentParts();

    abstract ImmutableMap<CommandPart, CommandValue> values();

    abstract InjectedValueAccess injectedValues();

    abstract CommandMetadata metadata();

    abstract ArgumentConverterAccess converters();

    @Override
    public final boolean has(CommandPart part) {
        return this.presentParts().contains((Object)part);
    }

    @Override
    public final CommandValue valueOf(ArgAcceptingCommandPart part) {
        CommandValue value = (CommandValue)this.values().get((Object)part);
        if (value == null) {
            throw new NoSuchElementException("No value for " + part);
        }
        return value;
    }

    @Override
    public CommandMetadata getMetadata() {
        return this.metadata();
    }

    @Override
    public ArgumentConverterAccess getConverters() {
        return this.converters();
    }

    @Override
    public final <T> Optional<T> injectedValue(Key<T> key, InjectedValueAccess context) {
        return this.injectedValues().injectedValue(key, context);
    }

    @AutoValue.Builder
    static interface Builder {
        default public Builder addPresentPart(CommandPart part) {
            this.presentPartsBuilder().add((Object)part);
            return this;
        }

        public ImmutableSet.Builder<CommandPart> presentPartsBuilder();

        default public Builder addValue(CommandPart part, CommandValue value) {
            this.valuesBuilder().put((Object)part, (Object)value);
            return this;
        }

        public ImmutableMap.Builder<CommandPart, CommandValue> valuesBuilder();

        public Builder injectedValues(InjectedValueAccess var1);

        public Builder metadata(CommandMetadata var1);

        public Builder converters(ArgumentConverterAccess var1);

        public CommandParametersImpl build();
    }
}

