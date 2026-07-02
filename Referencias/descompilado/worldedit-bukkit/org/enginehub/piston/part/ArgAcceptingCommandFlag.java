/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston.part;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.Collection;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.ArgAcceptingCommandPart;
import org.enginehub.piston.part.AutoValue_ArgAcceptingCommandFlag;
import org.enginehub.piston.part.CommandFlag;

@AutoValue
public abstract class ArgAcceptingCommandFlag
implements CommandFlag,
ArgAcceptingCommandPart {
    public static Builder builder(char name, Component description) {
        return new AutoValue_ArgAcceptingCommandFlag.Builder().named(name).describedBy(description).defaultsTo((Iterable<String>)ImmutableList.of()).ofTypes((Collection<Key<?>>)ImmutableList.of());
    }

    ArgAcceptingCommandFlag() {
    }

    @Override
    public Component getTextRepresentation() {
        return ColorConfig.partWrapping().wrap(TextComponent.of("["), ColorConfig.mainText().wrap("-" + this.getName()), TextComponent.space(), TextComponent.of("<"), ColorConfig.mainText().wrap(this.getArgumentName()), TextComponent.of(">"), TextComponent.of("]"));
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public final Builder named(char name) {
            return this.name(name);
        }

        abstract Builder name(char var1);

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

        public final Builder argNamed(String name) {
            return this.argNamed(TranslatableComponent.of(name));
        }

        public final Builder argNamed(TranslatableComponent name) {
            return this.argumentName(name);
        }

        abstract Builder argumentName(TranslatableComponent var1);

        public abstract ArgAcceptingCommandFlag build();
    }
}

