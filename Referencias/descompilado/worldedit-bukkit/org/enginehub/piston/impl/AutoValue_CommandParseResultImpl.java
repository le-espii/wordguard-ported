/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package org.enginehub.piston.impl;

import com.google.common.collect.ImmutableList;
import org.enginehub.piston.ArgBinding;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.impl.CommandParseResultImpl;

final class AutoValue_CommandParseResultImpl
extends CommandParseResultImpl {
    private final ImmutableList<Command> executionPath;
    private final ImmutableList<ArgBinding> boundArguments;
    private final CommandParameters parameters;

    private AutoValue_CommandParseResultImpl(ImmutableList<Command> executionPath, ImmutableList<ArgBinding> boundArguments, CommandParameters parameters) {
        this.executionPath = executionPath;
        this.boundArguments = boundArguments;
        this.parameters = parameters;
    }

    @Override
    public ImmutableList<Command> getExecutionPath() {
        return this.executionPath;
    }

    @Override
    public ImmutableList<ArgBinding> getBoundArguments() {
        return this.boundArguments;
    }

    @Override
    public CommandParameters getParameters() {
        return this.parameters;
    }

    public String toString() {
        return "CommandParseResultImpl{executionPath=" + this.executionPath + ", boundArguments=" + this.boundArguments + ", parameters=" + this.parameters + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CommandParseResultImpl) {
            CommandParseResultImpl that = (CommandParseResultImpl)o;
            return this.executionPath.equals(that.getExecutionPath()) && this.boundArguments.equals(that.getBoundArguments()) && this.parameters.equals(that.getParameters());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.executionPath.hashCode();
        h$ *= 1000003;
        h$ ^= this.boundArguments.hashCode();
        h$ *= 1000003;
        return h$ ^= this.parameters.hashCode();
    }

    static final class Builder
    extends CommandParseResultImpl.Builder {
        private ImmutableList.Builder<Command> executionPathBuilder$;
        private ImmutableList<Command> executionPath;
        private ImmutableList.Builder<ArgBinding> boundArgumentsBuilder$;
        private ImmutableList<ArgBinding> boundArguments;
        private CommandParameters parameters;

        Builder() {
        }

        @Override
        ImmutableList.Builder<Command> executionPathBuilder() {
            if (this.executionPathBuilder$ == null) {
                this.executionPathBuilder$ = ImmutableList.builder();
            }
            return this.executionPathBuilder$;
        }

        @Override
        public ImmutableList<Command> getExecutionPath() {
            if (this.executionPathBuilder$ != null) {
                return this.executionPathBuilder$.build();
            }
            if (this.executionPath == null) {
                this.executionPath = ImmutableList.of();
            }
            return this.executionPath;
        }

        @Override
        ImmutableList.Builder<ArgBinding> boundArgumentsBuilder() {
            if (this.boundArgumentsBuilder$ == null) {
                this.boundArgumentsBuilder$ = ImmutableList.builder();
            }
            return this.boundArgumentsBuilder$;
        }

        @Override
        public CommandParseResultImpl.Builder parameters(CommandParameters parameters) {
            if (parameters == null) {
                throw new NullPointerException("Null parameters");
            }
            this.parameters = parameters;
            return this;
        }

        @Override
        public CommandParseResultImpl build() {
            if (this.executionPathBuilder$ != null) {
                this.executionPath = this.executionPathBuilder$.build();
            } else if (this.executionPath == null) {
                this.executionPath = ImmutableList.of();
            }
            if (this.boundArgumentsBuilder$ != null) {
                this.boundArguments = this.boundArgumentsBuilder$.build();
            } else if (this.boundArguments == null) {
                this.boundArguments = ImmutableList.of();
            }
            if (this.parameters == null) {
                String missing = " parameters";
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_CommandParseResultImpl(this.executionPath, this.boundArguments, this.parameters);
        }
    }
}

