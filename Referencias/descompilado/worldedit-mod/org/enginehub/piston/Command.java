/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package org.enginehub.piston;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.suggestion.SuggestionProvider;
import org.enginehub.piston.util.HelpGenerator;

public interface Command {
    public String getName();

    public ImmutableList<String> getAliases();

    public Component getDescription();

    public Optional<Component> getFooter();

    public ImmutableList<CommandPart> getParts();

    public Condition getCondition();

    public Action getAction();

    public SuggestionProvider getSuggester();

    public Builder toBuilder();

    default public Component getUsage() {
        return HelpGenerator.create((Iterable<Command>)ImmutableList.of((Object)this)).getUsage();
    }

    default public Component getFullHelp() {
        return HelpGenerator.create((Iterable<Command>)ImmutableList.of((Object)this)).getFullHelp();
    }

    public static interface Builder {
        public Builder name(String var1);

        public Builder aliases(Collection<String> var1);

        public Builder description(Component var1);

        public Builder footer(@Nullable Component var1);

        public Builder parts(Collection<CommandPart> var1);

        public Builder addPart(CommandPart var1);

        public Builder addParts(CommandPart ... var1);

        public Builder addParts(Iterable<CommandPart> var1);

        public Builder action(Action var1);

        public Builder condition(Condition var1);

        public Builder suggester(SuggestionProvider var1);

        public Command build();
    }

    public static interface Condition {
        public static final Condition TRUE = p -> true;
        public static final Condition FALSE = p -> false;

        public boolean satisfied(InjectedValueAccess var1);

        default public Condition and(Condition other) {
            return c -> this.satisfied(c) && other.satisfied(c);
        }

        default public Condition or(Condition other) {
            return c -> this.satisfied(c) || other.satisfied(c);
        }

        default public Condition not() {
            return c -> !this.satisfied(c);
        }

        default public <T extends Condition> Optional<T> as(Class<T> type) {
            if (type.isInstance(this)) {
                return Optional.of((Condition)type.cast(this));
            }
            return Optional.empty();
        }
    }

    public static interface Action {
        public static final Action NULL_ACTION = params -> 0;

        public int run(CommandParameters var1) throws Exception;
    }
}

