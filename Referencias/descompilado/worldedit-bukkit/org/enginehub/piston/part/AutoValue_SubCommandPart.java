/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.part;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.Collection;
import org.enginehub.piston.Command;
import org.enginehub.piston.part.SubCommandPart;

final class AutoValue_SubCommandPart
extends SubCommandPart {
    private final Component description;
    private final boolean required;
    private final TranslatableComponent argumentName;
    private final ImmutableList<Command> commands;

    private AutoValue_SubCommandPart(Component description, boolean required, TranslatableComponent argumentName, ImmutableList<Command> commands) {
        this.description = description;
        this.required = required;
        this.argumentName = argumentName;
        this.commands = commands;
    }

    @Override
    public Component getDescription() {
        return this.description;
    }

    @Override
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public TranslatableComponent getArgumentName() {
        return this.argumentName;
    }

    @Override
    public ImmutableList<Command> getCommands() {
        return this.commands;
    }

    public String toString() {
        return "SubCommandPart{description=" + this.description + ", required=" + this.required + ", argumentName=" + this.argumentName + ", commands=" + this.commands + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SubCommandPart) {
            SubCommandPart that = (SubCommandPart)o;
            return this.description.equals(that.getDescription()) && this.required == that.isRequired() && this.argumentName.equals(that.getArgumentName()) && this.commands.equals(that.getCommands());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.description.hashCode();
        h$ *= 1000003;
        h$ ^= this.required ? 1231 : 1237;
        h$ *= 1000003;
        h$ ^= this.argumentName.hashCode();
        h$ *= 1000003;
        return h$ ^= this.commands.hashCode();
    }

    static final class Builder
    extends SubCommandPart.Builder {
        private Component description;
        private boolean required;
        private TranslatableComponent argumentName;
        private ImmutableList<Command> commands;
        private byte set$0;

        Builder() {
        }

        @Override
        SubCommandPart.Builder description(Component description) {
            if (description == null) {
                throw new NullPointerException("Null description");
            }
            this.description = description;
            return this;
        }

        @Override
        SubCommandPart.Builder required(boolean required) {
            this.required = required;
            this.set$0 = (byte)(this.set$0 | 1);
            return this;
        }

        @Override
        SubCommandPart.Builder argumentName(TranslatableComponent argumentName) {
            if (argumentName == null) {
                throw new NullPointerException("Null argumentName");
            }
            this.argumentName = argumentName;
            return this;
        }

        @Override
        SubCommandPart.Builder commands(Collection<Command> commands) {
            this.commands = ImmutableList.copyOf(commands);
            return this;
        }

        @Override
        public SubCommandPart build() {
            if (this.set$0 != 1 || this.description == null || this.argumentName == null || this.commands == null) {
                StringBuilder missing = new StringBuilder();
                if (this.description == null) {
                    missing.append(" description");
                }
                if ((this.set$0 & 1) == 0) {
                    missing.append(" required");
                }
                if (this.argumentName == null) {
                    missing.append(" argumentName");
                }
                if (this.commands == null) {
                    missing.append(" commands");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_SubCommandPart(this.description, this.required, this.argumentName, this.commands);
        }
    }
}

