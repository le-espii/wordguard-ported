/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.impl;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.Collection;
import javax.annotation.Nullable;
import org.enginehub.piston.Command;
import org.enginehub.piston.impl.AutoValue_CommandImpl;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.suggestion.DefaultSuggestionProvider;
import org.enginehub.piston.suggestion.SuggestionProvider;

@AutoValue
abstract class CommandImpl
implements Command {
    CommandImpl() {
    }

    static Builder builder(String name) {
        return new AutoValue_CommandImpl.Builder().footer(null).condition(Command.Condition.TRUE).name(name).aliases((Collection)ImmutableList.of()).parts((Collection)ImmutableList.of()).action(Command.Action.NULL_ACTION).suggester(DefaultSuggestionProvider.getInstance());
    }

    @Override
    public abstract Builder toBuilder();

    @AutoValue.Builder
    static interface Builder
    extends Command.Builder {
        @Override
        public Builder name(String var1);

        @Override
        public Builder aliases(Collection<String> var1);

        @Override
        public Builder description(Component var1);

        @Override
        public Builder footer(@Nullable Component var1);

        @Override
        public Builder parts(Collection<CommandPart> var1);

        @Override
        public Builder action(Command.Action var1);

        @Override
        public Builder condition(Command.Condition var1);

        @Override
        public Builder suggester(SuggestionProvider var1);

        public ImmutableList.Builder<CommandPart> partsBuilder();

        @Override
        default public Builder addPart(CommandPart part) {
            this.partsBuilder().add((Object)part);
            return this;
        }

        @Override
        default public Builder addParts(CommandPart ... parts) {
            this.partsBuilder().add((Object[])parts);
            return this;
        }

        @Override
        default public Builder addParts(Iterable<CommandPart> parts) {
            this.partsBuilder().addAll(parts);
            return this;
        }

        public CommandImpl autoBuild();

        @Override
        default public CommandImpl build() {
            CommandImpl auto = this.autoBuild();
            Preconditions.checkState((auto.getName().length() > 0 ? 1 : 0) != 0, (Object)"command name must not be empty");
            return auto;
        }
    }
}

