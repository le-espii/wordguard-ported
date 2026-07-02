/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package org.enginehub.piston.part;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.Collection;
import java.util.List;
import org.enginehub.piston.Command;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.part.ArgConsumingCommandPart;
import org.enginehub.piston.part.AutoValue_SubCommandPart;
import org.enginehub.piston.util.ComponentHelper;

@AutoValue
public abstract class SubCommandPart
implements ArgConsumingCommandPart {
    public static Builder builder(TranslatableComponent name, Component description) {
        return new AutoValue_SubCommandPart.Builder().named(name).describedBy(description);
    }

    public abstract ImmutableList<Command> getCommands();

    @Override
    public Component getTextRepresentation() {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)TextComponent.of(this.isRequired() ? "<" : "["));
        builder.addAll(this.getCommands().stream().map(Command::getName).map(ColorConfig.mainText()::wrap).collect(ComponentHelper.joiningWithBar()).children());
        builder.add((Object)TextComponent.of(this.isRequired() ? ">" : "]"));
        return ColorConfig.partWrapping().wrap((List<Component>)builder.build());
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public final Builder named(TranslatableComponent name) {
            return this.argumentName(name);
        }

        abstract Builder argumentName(TranslatableComponent var1);

        public final Builder describedBy(String description) {
            return this.describedBy(TextComponent.of(description));
        }

        public final Builder describedBy(Component description) {
            return this.description(description);
        }

        abstract Builder description(Component var1);

        public final Builder withCommands(Collection<Command> commands) {
            return this.commands(commands);
        }

        abstract Builder commands(Collection<Command> var1);

        public final Builder required() {
            return this.required(true);
        }

        public final Builder optional() {
            return this.required(false);
        }

        abstract Builder required(boolean var1);

        public abstract SubCommandPart build();
    }
}

