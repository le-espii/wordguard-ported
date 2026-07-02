/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package org.enginehub.piston.part;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.Collection;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.CommandArgument;

final class AutoValue_CommandArgument
extends CommandArgument {
    private final Component description;
    private final TranslatableComponent argumentName;
    private final ImmutableSet<Key<?>> types;
    private final ImmutableList<String> defaults;
    private final boolean variable;

    private AutoValue_CommandArgument(Component description, TranslatableComponent argumentName, ImmutableSet<Key<?>> types, ImmutableList<String> defaults, boolean variable) {
        this.description = description;
        this.argumentName = argumentName;
        this.types = types;
        this.defaults = defaults;
        this.variable = variable;
    }

    @Override
    public Component getDescription() {
        return this.description;
    }

    @Override
    public TranslatableComponent getArgumentName() {
        return this.argumentName;
    }

    @Override
    public ImmutableSet<Key<?>> getTypes() {
        return this.types;
    }

    @Override
    public ImmutableList<String> getDefaults() {
        return this.defaults;
    }

    @Override
    public boolean isVariable() {
        return this.variable;
    }

    public String toString() {
        return "CommandArgument{description=" + this.description + ", argumentName=" + this.argumentName + ", types=" + this.types + ", defaults=" + this.defaults + ", variable=" + this.variable + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CommandArgument) {
            CommandArgument that = (CommandArgument)o;
            return this.description.equals(that.getDescription()) && this.argumentName.equals(that.getArgumentName()) && this.types.equals(that.getTypes()) && this.defaults.equals(that.getDefaults()) && this.variable == that.isVariable();
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.description.hashCode();
        h$ *= 1000003;
        h$ ^= this.argumentName.hashCode();
        h$ *= 1000003;
        h$ ^= this.types.hashCode();
        h$ *= 1000003;
        h$ ^= this.defaults.hashCode();
        h$ *= 1000003;
        return h$ ^= this.variable ? 1231 : 1237;
    }

    static final class Builder
    extends CommandArgument.Builder {
        private Component description;
        private TranslatableComponent argumentName;
        private ImmutableSet<Key<?>> types;
        private ImmutableList<String> defaults;
        private boolean variable;
        private byte set$0;

        Builder() {
        }

        @Override
        CommandArgument.Builder description(Component description) {
            if (description == null) {
                throw new NullPointerException("Null description");
            }
            this.description = description;
            return this;
        }

        @Override
        CommandArgument.Builder argumentName(TranslatableComponent argumentName) {
            if (argumentName == null) {
                throw new NullPointerException("Null argumentName");
            }
            this.argumentName = argumentName;
            return this;
        }

        @Override
        CommandArgument.Builder types(Collection<Key<?>> types) {
            this.types = ImmutableSet.copyOf(types);
            return this;
        }

        @Override
        CommandArgument.Builder defaults(Iterable<String> defaults) {
            this.defaults = ImmutableList.copyOf(defaults);
            return this;
        }

        @Override
        public CommandArgument.Builder variable(boolean variable) {
            this.variable = variable;
            this.set$0 = (byte)(this.set$0 | 1);
            return this;
        }

        @Override
        public CommandArgument build() {
            if (this.set$0 != 1 || this.description == null || this.argumentName == null || this.types == null || this.defaults == null) {
                StringBuilder missing = new StringBuilder();
                if (this.description == null) {
                    missing.append(" description");
                }
                if (this.argumentName == null) {
                    missing.append(" argumentName");
                }
                if (this.types == null) {
                    missing.append(" types");
                }
                if (this.defaults == null) {
                    missing.append(" defaults");
                }
                if ((this.set$0 & 1) == 0) {
                    missing.append(" variable");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_CommandArgument(this.description, this.argumentName, this.types, this.defaults, this.variable);
        }
    }
}

