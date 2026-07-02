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
import org.enginehub.piston.part.ArgAcceptingCommandFlag;

final class AutoValue_ArgAcceptingCommandFlag
extends ArgAcceptingCommandFlag {
    private final Component description;
    private final char name;
    private final TranslatableComponent argumentName;
    private final ImmutableSet<Key<?>> types;
    private final ImmutableList<String> defaults;

    private AutoValue_ArgAcceptingCommandFlag(Component description, char name, TranslatableComponent argumentName, ImmutableSet<Key<?>> types, ImmutableList<String> defaults) {
        this.description = description;
        this.name = name;
        this.argumentName = argumentName;
        this.types = types;
        this.defaults = defaults;
    }

    @Override
    public Component getDescription() {
        return this.description;
    }

    @Override
    public char getName() {
        return this.name;
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

    public String toString() {
        return "ArgAcceptingCommandFlag{description=" + this.description + ", name=" + this.name + ", argumentName=" + this.argumentName + ", types=" + this.types + ", defaults=" + this.defaults + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ArgAcceptingCommandFlag) {
            ArgAcceptingCommandFlag that = (ArgAcceptingCommandFlag)o;
            return this.description.equals(that.getDescription()) && this.name == that.getName() && this.argumentName.equals(that.getArgumentName()) && this.types.equals(that.getTypes()) && this.defaults.equals(that.getDefaults());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.description.hashCode();
        h$ *= 1000003;
        h$ ^= this.name;
        h$ *= 1000003;
        h$ ^= this.argumentName.hashCode();
        h$ *= 1000003;
        h$ ^= this.types.hashCode();
        h$ *= 1000003;
        return h$ ^= this.defaults.hashCode();
    }

    static final class Builder
    extends ArgAcceptingCommandFlag.Builder {
        private Component description;
        private char name;
        private TranslatableComponent argumentName;
        private ImmutableSet<Key<?>> types;
        private ImmutableList<String> defaults;
        private byte set$0;

        Builder() {
        }

        @Override
        ArgAcceptingCommandFlag.Builder description(Component description) {
            if (description == null) {
                throw new NullPointerException("Null description");
            }
            this.description = description;
            return this;
        }

        @Override
        ArgAcceptingCommandFlag.Builder name(char name) {
            this.name = name;
            this.set$0 = (byte)(this.set$0 | 1);
            return this;
        }

        @Override
        ArgAcceptingCommandFlag.Builder argumentName(TranslatableComponent argumentName) {
            if (argumentName == null) {
                throw new NullPointerException("Null argumentName");
            }
            this.argumentName = argumentName;
            return this;
        }

        @Override
        ArgAcceptingCommandFlag.Builder types(Collection<Key<?>> types) {
            this.types = ImmutableSet.copyOf(types);
            return this;
        }

        @Override
        ArgAcceptingCommandFlag.Builder defaults(Iterable<String> defaults) {
            this.defaults = ImmutableList.copyOf(defaults);
            return this;
        }

        @Override
        public ArgAcceptingCommandFlag build() {
            if (this.set$0 != 1 || this.description == null || this.argumentName == null || this.types == null || this.defaults == null) {
                StringBuilder missing = new StringBuilder();
                if (this.description == null) {
                    missing.append(" description");
                }
                if ((this.set$0 & 1) == 0) {
                    missing.append(" name");
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
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_ArgAcceptingCommandFlag(this.description, this.name, this.argumentName, this.types, this.defaults);
        }
    }
}

