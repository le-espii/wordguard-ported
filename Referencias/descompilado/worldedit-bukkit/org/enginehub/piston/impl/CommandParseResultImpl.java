/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package org.enginehub.piston.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.enginehub.piston.ArgBinding;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.impl.AutoValue_CommandParseResultImpl;

@AutoValue
abstract class CommandParseResultImpl
implements CommandParseResult {
    static Builder builder() {
        return new AutoValue_CommandParseResultImpl.Builder();
    }

    CommandParseResultImpl() {
    }

    @Override
    public abstract ImmutableList<Command> getExecutionPath();

    @Override
    public abstract ImmutableList<ArgBinding> getBoundArguments();

    @Override
    public abstract CommandParameters getParameters();

    @AutoValue.Builder
    static abstract class Builder {
        Builder() {
        }

        public final Builder addCommand(Command command) {
            this.executionPathBuilder().add((Object)command);
            return this;
        }

        abstract ImmutableList.Builder<Command> executionPathBuilder();

        public abstract ImmutableList<Command> getExecutionPath();

        public final Builder addArgument(ArgBinding argBinding) {
            this.boundArgumentsBuilder().add((Object)argBinding);
            return this;
        }

        abstract ImmutableList.Builder<ArgBinding> boundArgumentsBuilder();

        public abstract Builder parameters(CommandParameters var1);

        public abstract CommandParseResultImpl build();
    }
}

