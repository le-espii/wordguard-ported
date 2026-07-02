/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.impl;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.function.Supplier;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.impl.AutoValue_CommandValueImpl;
import org.enginehub.piston.impl.CommandValueImpl;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.part.CommandPart;

abstract class $AutoValue_CommandValueImpl
extends CommandValueImpl {
    private final ArgumentConverterAccess manager;
    private final Supplier<CommandParseResult> commandContextSupplier;
    private final CommandPart partContext;
    private final InjectedValueAccess injectedValues;
    private final ImmutableList<String> values;

    $AutoValue_CommandValueImpl(ArgumentConverterAccess manager, Supplier<CommandParseResult> commandContextSupplier, CommandPart partContext, InjectedValueAccess injectedValues, ImmutableList<String> values) {
        if (manager == null) {
            throw new NullPointerException("Null manager");
        }
        this.manager = manager;
        if (commandContextSupplier == null) {
            throw new NullPointerException("Null commandContextSupplier");
        }
        this.commandContextSupplier = commandContextSupplier;
        if (partContext == null) {
            throw new NullPointerException("Null partContext");
        }
        this.partContext = partContext;
        if (injectedValues == null) {
            throw new NullPointerException("Null injectedValues");
        }
        this.injectedValues = injectedValues;
        if (values == null) {
            throw new NullPointerException("Null values");
        }
        this.values = values;
    }

    @Override
    ArgumentConverterAccess manager() {
        return this.manager;
    }

    @Override
    Supplier<CommandParseResult> commandContextSupplier() {
        return this.commandContextSupplier;
    }

    @Override
    CommandPart partContext() {
        return this.partContext;
    }

    @Override
    InjectedValueAccess injectedValues() {
        return this.injectedValues;
    }

    @Override
    ImmutableList<String> values() {
        return this.values;
    }

    public String toString() {
        return "CommandValueImpl{manager=" + this.manager + ", commandContextSupplier=" + this.commandContextSupplier + ", partContext=" + this.partContext + ", injectedValues=" + this.injectedValues + ", values=" + this.values + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CommandValueImpl) {
            CommandValueImpl that = (CommandValueImpl)o;
            return this.manager.equals(that.manager()) && this.commandContextSupplier.equals(that.commandContextSupplier()) && this.partContext.equals(that.partContext()) && this.injectedValues.equals(that.injectedValues()) && this.values.equals(that.values());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.manager.hashCode();
        h$ *= 1000003;
        h$ ^= this.commandContextSupplier.hashCode();
        h$ *= 1000003;
        h$ ^= this.partContext.hashCode();
        h$ *= 1000003;
        h$ ^= this.injectedValues.hashCode();
        h$ *= 1000003;
        return h$ ^= this.values.hashCode();
    }

    static class Builder
    implements CommandValueImpl.Builder {
        private ArgumentConverterAccess manager;
        private Supplier<CommandParseResult> commandContextSupplier;
        private CommandPart partContext;
        private InjectedValueAccess injectedValues;
        private ImmutableList<String> values;

        Builder() {
        }

        @Override
        public CommandValueImpl.Builder manager(ArgumentConverterAccess manager) {
            if (manager == null) {
                throw new NullPointerException("Null manager");
            }
            this.manager = manager;
            return this;
        }

        @Override
        public CommandValueImpl.Builder commandContextSupplier(Supplier<CommandParseResult> commandContextSupplier) {
            if (commandContextSupplier == null) {
                throw new NullPointerException("Null commandContextSupplier");
            }
            this.commandContextSupplier = commandContextSupplier;
            return this;
        }

        @Override
        public CommandValueImpl.Builder partContext(CommandPart partContext) {
            if (partContext == null) {
                throw new NullPointerException("Null partContext");
            }
            this.partContext = partContext;
            return this;
        }

        @Override
        public CommandValueImpl.Builder injectedValues(InjectedValueAccess injectedValues) {
            if (injectedValues == null) {
                throw new NullPointerException("Null injectedValues");
            }
            this.injectedValues = injectedValues;
            return this;
        }

        @Override
        public CommandValueImpl.Builder values(Collection<String> values) {
            this.values = ImmutableList.copyOf(values);
            return this;
        }

        @Override
        public CommandValueImpl build() {
            if (this.manager == null || this.commandContextSupplier == null || this.partContext == null || this.injectedValues == null || this.values == null) {
                StringBuilder missing = new StringBuilder();
                if (this.manager == null) {
                    missing.append(" manager");
                }
                if (this.commandContextSupplier == null) {
                    missing.append(" commandContextSupplier");
                }
                if (this.partContext == null) {
                    missing.append(" partContext");
                }
                if (this.injectedValues == null) {
                    missing.append(" injectedValues");
                }
                if (this.values == null) {
                    missing.append(" values");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_CommandValueImpl(this.manager, this.commandContextSupplier, this.partContext, this.injectedValues, this.values);
        }
    }
}

