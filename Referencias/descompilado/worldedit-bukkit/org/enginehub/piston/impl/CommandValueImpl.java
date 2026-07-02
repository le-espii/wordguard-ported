/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.auto.value.extension.memoized.Memoized
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package org.enginehub.piston.impl;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.CommandValue;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.exception.ConversionFailedException;
import org.enginehub.piston.impl.$AutoValue_CommandValueImpl;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.CommandPart;

@AutoValue
abstract class CommandValueImpl
implements CommandValue {
    static Builder builder() {
        return new $AutoValue_CommandValueImpl.Builder();
    }

    CommandValueImpl() {
    }

    abstract ArgumentConverterAccess manager();

    abstract Supplier<CommandParseResult> commandContextSupplier();

    @Memoized
    CommandParseResult commandContext() {
        return this.commandContextSupplier().get();
    }

    abstract CommandPart partContext();

    abstract InjectedValueAccess injectedValues();

    abstract ImmutableList<String> values();

    @Override
    public ImmutableList<String> asStrings() {
        return this.values();
    }

    @Override
    public <T> ImmutableList<T> asMultiple(Key<T> key) {
        ImmutableList.Builder values = ImmutableList.builder();
        for (String value : this.values()) {
            if (value.trim().isEmpty()) continue;
            Optional<ArgumentConverter<T>> converter = this.manager().getConverter(key);
            Preconditions.checkState((boolean)converter.isPresent(), (String)"No converter for %s", key);
            ConversionResult convert = converter.get().convert(value, this.injectedValues());
            if (!convert.isSuccessful()) {
                throw new ConversionFailedException(this.commandContext(), this.partContext().getTextRepresentation(), converter.get(), (FailedConversion)convert);
            }
            values.addAll(convert.get());
        }
        return values.build();
    }

    @AutoValue.Builder
    static interface Builder {
        public Builder manager(ArgumentConverterAccess var1);

        public Builder commandContextSupplier(Supplier<CommandParseResult> var1);

        public Builder partContext(CommandPart var1);

        public Builder injectedValues(InjectedValueAccess var1);

        default public Builder value(String value) {
            return this.values((Collection<String>)ImmutableList.of((Object)value));
        }

        public Builder values(Collection<String> var1);

        public CommandValueImpl build();
    }
}

