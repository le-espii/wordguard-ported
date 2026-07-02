/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 */
package org.enginehub.piston.part;

import com.google.auto.value.AutoValue;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.part.ArgAcceptingCommandFlag;
import org.enginehub.piston.part.AutoValue_NoArgCommandFlag;
import org.enginehub.piston.part.CommandFlag;

@AutoValue
public abstract class NoArgCommandFlag
implements CommandFlag {
    public static Builder builder(char name, String description) {
        return NoArgCommandFlag.builder(name, TextComponent.of(description));
    }

    public static Builder builder(char name, Component description) {
        return new AutoValue_NoArgCommandFlag.Builder().named(name).describedBy(description);
    }

    NoArgCommandFlag() {
    }

    @Override
    public Component getTextRepresentation() {
        return ColorConfig.partWrapping().wrap(TextComponent.of("["), ColorConfig.mainText().wrap("-" + this.getName()), TextComponent.of("]"));
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

        public final ArgAcceptingCommandFlag.Builder withRequiredArg() {
            NoArgCommandFlag flag = this.build();
            return ArgAcceptingCommandFlag.builder(flag.getName(), flag.getDescription());
        }

        public abstract NoArgCommandFlag build();
    }
}

