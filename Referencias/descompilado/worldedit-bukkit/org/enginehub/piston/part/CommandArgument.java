/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 */
package org.enginehub.piston.part;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.Collection;
import java.util.List;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.ArgAcceptingCommandPart;
import org.enginehub.piston.part.AutoValue_CommandArgument;

@AutoValue
public abstract class CommandArgument
implements ArgAcceptingCommandPart {
    public static Builder builder(TranslatableComponent name, Component description) {
        return new AutoValue_CommandArgument.Builder().named(name).describedBy(description).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableSet.of()).variable(false);
    }

    public abstract boolean isVariable();

    @Override
    public final boolean isRequired() {
        return this.getDefaults().isEmpty();
    }

    @Override
    public Component getTextRepresentation() {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)TextComponent.of(this.isRequired() ? "<" : "["));
        builder.add((Object)ColorConfig.mainText().wrap(this.getArgumentName()));
        if (this.isVariable()) {
            builder.add((Object)ColorConfig.textModifier().wrap("..."));
        }
        builder.add((Object)TextComponent.of(this.isRequired() ? ">" : "]"));
        return ColorConfig.partWrapping().wrap((List<Component>)builder.build());
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public final Builder named(String name) {
            return this.named(TranslatableComponent.of(name));
        }

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

        public final Builder defaultsTo(Iterable<String> defaults) {
            return this.defaults(defaults);
        }

        abstract Builder defaults(Iterable<String> var1);

        public final Builder ofTypes(Collection<Key<?>> types) {
            return this.types(types);
        }

        abstract Builder types(Collection<Key<?>> var1);

        public abstract Builder variable(boolean var1);

        public abstract CommandArgument build();
    }
}

