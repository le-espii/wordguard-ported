/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.impl;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import org.enginehub.piston.Command;
import org.enginehub.piston.impl.CommandImpl;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.suggestion.SuggestionProvider;

final class AutoValue_CommandImpl
extends CommandImpl {
    private final String name;
    private final ImmutableList<String> aliases;
    private final Component description;
    private final Optional<Component> footer;
    private final ImmutableList<CommandPart> parts;
    private final Command.Condition condition;
    private final Command.Action action;
    private final SuggestionProvider suggester;

    private AutoValue_CommandImpl(String name, ImmutableList<String> aliases, Component description, Optional<Component> footer, ImmutableList<CommandPart> parts, Command.Condition condition, Command.Action action, SuggestionProvider suggester) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.footer = footer;
        this.parts = parts;
        this.condition = condition;
        this.action = action;
        this.suggester = suggester;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ImmutableList<String> getAliases() {
        return this.aliases;
    }

    @Override
    public Component getDescription() {
        return this.description;
    }

    @Override
    public Optional<Component> getFooter() {
        return this.footer;
    }

    @Override
    public ImmutableList<CommandPart> getParts() {
        return this.parts;
    }

    @Override
    public Command.Condition getCondition() {
        return this.condition;
    }

    @Override
    public Command.Action getAction() {
        return this.action;
    }

    @Override
    public SuggestionProvider getSuggester() {
        return this.suggester;
    }

    public String toString() {
        return "CommandImpl{name=" + this.name + ", aliases=" + this.aliases + ", description=" + this.description + ", footer=" + this.footer + ", parts=" + this.parts + ", condition=" + this.condition + ", action=" + this.action + ", suggester=" + this.suggester + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CommandImpl) {
            CommandImpl that = (CommandImpl)o;
            return this.name.equals(that.getName()) && this.aliases.equals(that.getAliases()) && this.description.equals(that.getDescription()) && this.footer.equals(that.getFooter()) && this.parts.equals(that.getParts()) && this.condition.equals(that.getCondition()) && this.action.equals(that.getAction()) && this.suggester.equals(that.getSuggester());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.name.hashCode();
        h$ *= 1000003;
        h$ ^= this.aliases.hashCode();
        h$ *= 1000003;
        h$ ^= this.description.hashCode();
        h$ *= 1000003;
        h$ ^= this.footer.hashCode();
        h$ *= 1000003;
        h$ ^= this.parts.hashCode();
        h$ *= 1000003;
        h$ ^= this.condition.hashCode();
        h$ *= 1000003;
        h$ ^= this.action.hashCode();
        h$ *= 1000003;
        return h$ ^= this.suggester.hashCode();
    }

    @Override
    public CommandImpl.Builder toBuilder() {
        return new Builder(this);
    }

    static final class Builder
    implements CommandImpl.Builder {
        private String name;
        private ImmutableList<String> aliases;
        private Component description;
        private Optional<Component> footer = Optional.empty();
        private ImmutableList.Builder<CommandPart> partsBuilder$;
        private ImmutableList<CommandPart> parts;
        private Command.Condition condition;
        private Command.Action action;
        private SuggestionProvider suggester;

        Builder() {
        }

        private Builder(CommandImpl source) {
            this.name = source.getName();
            this.aliases = source.getAliases();
            this.description = source.getDescription();
            this.footer = source.getFooter();
            this.parts = source.getParts();
            this.condition = source.getCondition();
            this.action = source.getAction();
            this.suggester = source.getSuggester();
        }

        @Override
        public CommandImpl.Builder name(String name) {
            if (name == null) {
                throw new NullPointerException("Null name");
            }
            this.name = name;
            return this;
        }

        @Override
        public CommandImpl.Builder aliases(Collection<String> aliases) {
            this.aliases = ImmutableList.copyOf(aliases);
            return this;
        }

        @Override
        public CommandImpl.Builder description(Component description) {
            if (description == null) {
                throw new NullPointerException("Null description");
            }
            this.description = description;
            return this;
        }

        @Override
        public CommandImpl.Builder footer(@Nullable Component footer) {
            this.footer = Optional.ofNullable(footer);
            return this;
        }

        @Override
        public CommandImpl.Builder parts(Collection<CommandPart> parts) {
            if (this.partsBuilder$ != null) {
                throw new IllegalStateException("Cannot set parts after calling partsBuilder()");
            }
            this.parts = ImmutableList.copyOf(parts);
            return this;
        }

        @Override
        public ImmutableList.Builder<CommandPart> partsBuilder() {
            if (this.partsBuilder$ == null) {
                if (this.parts == null) {
                    this.partsBuilder$ = ImmutableList.builder();
                } else {
                    this.partsBuilder$ = ImmutableList.builder();
                    this.partsBuilder$.addAll(this.parts);
                    this.parts = null;
                }
            }
            return this.partsBuilder$;
        }

        @Override
        public CommandImpl.Builder condition(Command.Condition condition) {
            if (condition == null) {
                throw new NullPointerException("Null condition");
            }
            this.condition = condition;
            return this;
        }

        @Override
        public CommandImpl.Builder action(Command.Action action) {
            if (action == null) {
                throw new NullPointerException("Null action");
            }
            this.action = action;
            return this;
        }

        @Override
        public CommandImpl.Builder suggester(SuggestionProvider suggester) {
            if (suggester == null) {
                throw new NullPointerException("Null suggester");
            }
            this.suggester = suggester;
            return this;
        }

        @Override
        public CommandImpl autoBuild() {
            if (this.partsBuilder$ != null) {
                this.parts = this.partsBuilder$.build();
            } else if (this.parts == null) {
                this.parts = ImmutableList.of();
            }
            if (this.name == null || this.aliases == null || this.description == null || this.condition == null || this.action == null || this.suggester == null) {
                StringBuilder missing = new StringBuilder();
                if (this.name == null) {
                    missing.append(" name");
                }
                if (this.aliases == null) {
                    missing.append(" aliases");
                }
                if (this.description == null) {
                    missing.append(" description");
                }
                if (this.condition == null) {
                    missing.append(" condition");
                }
                if (this.action == null) {
                    missing.append(" action");
                }
                if (this.suggester == null) {
                    missing.append(" suggester");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_CommandImpl(this.name, this.aliases, this.description, this.footer, this.parts, this.condition, this.action, this.suggester);
        }
    }
}

