/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.format;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Style {
    private static final Style EMPTY = new Style(null, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, TextDecoration.State.NOT_SET, null, null, null);
    private static final TextDecoration[] DECORATIONS = TextDecoration.values();
    private final @Nullable TextColor color;
    private final TextDecoration.State obfuscated;
    private final TextDecoration.State bold;
    private final TextDecoration.State strikethrough;
    private final TextDecoration.State underlined;
    private final TextDecoration.State italic;
    private final @Nullable ClickEvent clickEvent;
    private final @Nullable HoverEvent hoverEvent;
    private final @Nullable String insertion;

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static @NonNull Style empty() {
        return EMPTY;
    }

    public static @NonNull Style of(@Nullable TextColor color) {
        if (color == null) {
            return Style.empty();
        }
        return Style.builder().color(color).build();
    }

    public static @NonNull Style of(@NonNull TextDecoration decoration) {
        return Style.builder().decoration(decoration, true).build();
    }

    public static @NonNull Style of(TextDecoration ... decorations) {
        if (decorations.length == 0) {
            return Style.empty();
        }
        Builder builder = Style.builder();
        Style.decorate(builder, decorations);
        return builder.build();
    }

    public static @NonNull Style of(@Nullable TextColor color, TextDecoration ... decorations) {
        Builder builder = Style.builder();
        builder.color(color);
        Style.decorate(builder, decorations);
        return builder.build();
    }

    private static void decorate(Builder builder, TextDecoration[] decorations) {
        for (TextDecoration decoration : decorations) {
            builder.decoration(decoration, true);
        }
    }

    public static @NonNull Style of(@Nullable TextColor color, Set<TextDecoration> decorations) {
        Builder builder = Style.builder();
        builder.color(color);
        if (!decorations.isEmpty()) {
            for (TextDecoration decoration : decorations) {
                builder.decoration(decoration, true);
            }
        }
        return builder.build();
    }

    public static @NonNull Style make(@NonNull Consumer<Builder> consumer) {
        Builder builder = Style.builder();
        consumer.accept(builder);
        return builder.build();
    }

    @Deprecated
    public static @NonNull Style of(@NonNull Consumer<Builder> consumer) {
        return Style.make(consumer);
    }

    private Style(@Nullable TextColor color, TextDecoration.State obfuscated, TextDecoration.State bold, TextDecoration.State strikethrough, TextDecoration.State underlined, TextDecoration.State italic, @Nullable ClickEvent clickEvent, @Nullable HoverEvent hoverEvent, @Nullable String insertion) {
        this.color = color;
        this.obfuscated = obfuscated;
        this.bold = bold;
        this.strikethrough = strikethrough;
        this.underlined = underlined;
        this.italic = italic;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        this.insertion = insertion;
    }

    public @NonNull Style edit(@NonNull Consumer<Builder> consumer) {
        return this.edit(consumer, Merge.Strategy.ALWAYS);
    }

    public @NonNull Style edit(@NonNull Consumer<Builder> consumer, @NonNull Merge.Strategy strategy) {
        return Style.make(style -> {
            if (strategy == Merge.Strategy.ALWAYS) {
                style.merge(this, strategy);
            }
            consumer.accept((Builder)style);
            if (strategy == Merge.Strategy.IF_ABSENT_ON_TARGET) {
                style.merge(this, strategy);
            }
        });
    }

    public @Nullable TextColor color() {
        return this.color;
    }

    public @NonNull Style colorIfAbsent(@Nullable TextColor color) {
        if (this.color == null) {
            return this.color(color);
        }
        return this;
    }

    public @NonNull Style color(@Nullable TextColor color) {
        if (Objects.equals(this.color, color)) {
            return this;
        }
        return new Style(color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
    }

    public boolean hasDecoration(@NonNull TextDecoration decoration) {
        return this.decoration(decoration) == TextDecoration.State.TRUE;
    }

    public @NonNull TextDecoration.State decoration(@NonNull TextDecoration decoration) {
        if (decoration == TextDecoration.BOLD) {
            return this.bold;
        }
        if (decoration == TextDecoration.ITALIC) {
            return this.italic;
        }
        if (decoration == TextDecoration.UNDERLINED) {
            return this.underlined;
        }
        if (decoration == TextDecoration.STRIKETHROUGH) {
            return this.strikethrough;
        }
        if (decoration == TextDecoration.OBFUSCATED) {
            return this.obfuscated;
        }
        throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
    }

    public @NonNull Style decoration(@NonNull TextDecoration decoration, boolean flag) {
        return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
    }

    public @NonNull Style decoration(@NonNull TextDecoration decoration, @NonNull TextDecoration.State state) {
        Objects.requireNonNull(state, "state");
        if (decoration == TextDecoration.BOLD) {
            return new Style(this.color, this.obfuscated, state, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }
        if (decoration == TextDecoration.ITALIC) {
            return new Style(this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, state, this.clickEvent, this.hoverEvent, this.insertion);
        }
        if (decoration == TextDecoration.UNDERLINED) {
            return new Style(this.color, this.obfuscated, this.bold, this.strikethrough, state, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }
        if (decoration == TextDecoration.STRIKETHROUGH) {
            return new Style(this.color, this.obfuscated, this.bold, state, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }
        if (decoration == TextDecoration.OBFUSCATED) {
            return new Style(this.color, state, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }
        throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
    }

    public @NonNull Set<TextDecoration> decorations() {
        return this.decorations(Collections.emptySet());
    }

    public @NonNull Set<TextDecoration> decorations(@NonNull Set<TextDecoration> defaultValues) {
        EnumSet<TextDecoration> decorations = EnumSet.noneOf(TextDecoration.class);
        for (TextDecoration decoration : DECORATIONS) {
            TextDecoration.State value = this.decoration(decoration);
            if (value != TextDecoration.State.TRUE && (value != TextDecoration.State.NOT_SET || !defaultValues.contains(decoration))) continue;
            decorations.add(decoration);
        }
        return decorations;
    }

    public @Nullable ClickEvent clickEvent() {
        return this.clickEvent;
    }

    public @NonNull Style clickEvent(@Nullable ClickEvent event) {
        return new Style(this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, event, this.hoverEvent, this.insertion);
    }

    public @Nullable HoverEvent hoverEvent() {
        return this.hoverEvent;
    }

    public @NonNull Style hoverEvent(@Nullable HoverEvent event) {
        return new Style(this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, event, this.insertion);
    }

    public @Nullable String insertion() {
        return this.insertion;
    }

    public @NonNull Style insertion(@Nullable String insertion) {
        if (Objects.equals(this.insertion, insertion)) {
            return this;
        }
        return new Style(this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, insertion);
    }

    public @NonNull Style merge(@NonNull Style that) {
        return this.merge(that, Merge.all());
    }

    public @NonNull Style merge(@NonNull Style that, @NonNull Merge.Strategy strategy) {
        return this.merge(that, strategy, Merge.all());
    }

    public @NonNull Style merge(@NonNull Style that, @NonNull Merge merge) {
        return this.merge(that, Collections.singleton(merge));
    }

    public @NonNull Style merge(@NonNull Style that, @NonNull Merge.Strategy strategy, @NonNull Merge merge) {
        return this.merge(that, strategy, Collections.singleton(merge));
    }

    public @NonNull Style merge(@NonNull Style that, Merge ... merges) {
        return this.merge(that, Merge.of(merges));
    }

    public @NonNull Style merge(@NonNull Style that, @NonNull Merge.Strategy strategy, Merge ... merges) {
        return this.merge(that, strategy, Merge.of(merges));
    }

    public @NonNull Style merge(@NonNull Style that, @NonNull Set<Merge> merges) {
        return this.merge(that, Merge.Strategy.ALWAYS, merges);
    }

    public @NonNull Style merge(@NonNull Style that, @NonNull Merge.Strategy strategy, @NonNull Set<Merge> merges) {
        if (that.isEmpty() || strategy == Merge.Strategy.NEVER || merges.isEmpty()) {
            return this;
        }
        if (this.isEmpty() && Merge.hasAll(merges)) {
            return that;
        }
        Builder builder = this.toBuilder();
        builder.merge(that, strategy, merges);
        return builder.build();
    }

    @Deprecated
    public @NonNull Style mergeColor(@NonNull Style that) {
        return this.merge(that, Merge.COLOR);
    }

    @Deprecated
    public @NonNull Style mergeDecorations(@NonNull Style that) {
        return this.merge(that, Merge.DECORATIONS);
    }

    @Deprecated
    public @NonNull Style mergeEvents(@NonNull Style that) {
        return this.merge(that, Merge.EVENTS);
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public @NonNull Builder toBuilder() {
        return new Builder(this);
    }

    public @NonNull String toString() {
        return ShadyPines.toString((Object)this, map -> {
            map.put("color", this.color);
            map.put("obfuscated", this.obfuscated);
            map.put("bold", this.bold);
            map.put("strikethrough", this.strikethrough);
            map.put("underlined", this.underlined);
            map.put("italic", this.italic);
            map.put("clickEvent", this.clickEvent);
            map.put("hoverEvent", this.hoverEvent);
            map.put("insertion", this.insertion);
        });
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Style)) {
            return false;
        }
        Style that = (Style)other;
        return Objects.equals(this.color, that.color) && this.obfuscated == that.obfuscated && this.bold == that.bold && this.strikethrough == that.strikethrough && this.underlined == that.underlined && this.italic == that.italic && Objects.equals(this.clickEvent, that.clickEvent) && Objects.equals(this.hoverEvent, that.hoverEvent) && Objects.equals(this.insertion, that.insertion);
    }

    public int hashCode() {
        int result = Objects.hashCode(this.color);
        result = 31 * result + this.obfuscated.hashCode();
        result = 31 * result + this.bold.hashCode();
        result = 31 * result + this.strikethrough.hashCode();
        result = 31 * result + this.underlined.hashCode();
        result = 31 * result + this.italic.hashCode();
        result = 31 * result + Objects.hashCode(this.clickEvent);
        result = 31 * result + Objects.hashCode(this.hoverEvent);
        result = 31 * result + Objects.hashCode(this.insertion);
        return result;
    }

    public static class Builder {
        private @Nullable TextColor color;
        private TextDecoration.State obfuscated = TextDecoration.State.NOT_SET;
        private TextDecoration.State bold = TextDecoration.State.NOT_SET;
        private TextDecoration.State strikethrough = TextDecoration.State.NOT_SET;
        private TextDecoration.State underlined = TextDecoration.State.NOT_SET;
        private TextDecoration.State italic = TextDecoration.State.NOT_SET;
        private @Nullable ClickEvent clickEvent;
        private @Nullable HoverEvent hoverEvent;
        private @Nullable String insertion;

        protected Builder() {
        }

        protected Builder(@NonNull Component component) {
            this(component.style());
        }

        protected Builder(@NonNull Style style) {
            this.color = style.color;
            this.obfuscated = style.obfuscated;
            this.bold = style.bold;
            this.strikethrough = style.strikethrough;
            this.underlined = style.underlined;
            this.italic = style.italic;
            this.clickEvent = style.clickEvent;
            this.hoverEvent = style.hoverEvent;
            this.insertion = style.insertion;
        }

        public @NonNull Builder color(@Nullable TextColor color) {
            this.color = color;
            return this;
        }

        public @NonNull Builder colorIfAbsent(@Nullable TextColor color) {
            if (this.color == null) {
                this.color = color;
            }
            return this;
        }

        public @NonNull Builder decoration(@NonNull TextDecoration decoration, boolean flag) {
            return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
        }

        public @NonNull Builder decoration(@NonNull TextDecoration decoration, @NonNull TextDecoration.State state) {
            Objects.requireNonNull(state, "state");
            if (decoration == TextDecoration.BOLD) {
                this.bold = state;
                return this;
            }
            if (decoration == TextDecoration.ITALIC) {
                this.italic = state;
                return this;
            }
            if (decoration == TextDecoration.UNDERLINED) {
                this.underlined = state;
                return this;
            }
            if (decoration == TextDecoration.STRIKETHROUGH) {
                this.strikethrough = state;
                return this;
            }
            if (decoration == TextDecoration.OBFUSCATED) {
                this.obfuscated = state;
                return this;
            }
            throw new IllegalArgumentException(String.format("unknown decoration '%s'", decoration));
        }

        public @NonNull Builder clickEvent(@Nullable ClickEvent event) {
            this.clickEvent = event;
            return this;
        }

        public @NonNull Builder hoverEvent(@Nullable HoverEvent event) {
            this.hoverEvent = event;
            return this;
        }

        public @NonNull Builder insertion(@Nullable String insertion) {
            this.insertion = insertion;
            return this;
        }

        public @NonNull Builder merge(@NonNull Style that) {
            return this.merge(that, Merge.all());
        }

        public @NonNull Builder merge(@NonNull Style that, @NonNull Merge.Strategy strategy) {
            return this.merge(that, strategy, Merge.all());
        }

        public @NonNull Builder merge(@NonNull Style that, Merge ... merges) {
            if (merges.length == 0) {
                return this;
            }
            return this.merge(that, Merge.of(merges));
        }

        public @NonNull Builder merge(@NonNull Style that, @NonNull Merge.Strategy strategy, Merge ... merges) {
            if (merges.length == 0) {
                return this;
            }
            return this.merge(that, strategy, Merge.of(merges));
        }

        public @NonNull Builder merge(@NonNull Style that, @NonNull Set<Merge> merges) {
            return this.merge(that, Merge.Strategy.ALWAYS, merges);
        }

        public @NonNull Builder merge(@NonNull Style that, @NonNull Merge.Strategy strategy, @NonNull Set<Merge> merges) {
            String insertion;
            TextColor color;
            if (that.isEmpty() || strategy == Merge.Strategy.NEVER || merges.isEmpty()) {
                return this;
            }
            if (merges.contains((Object)Merge.COLOR) && (color = that.color()) != null && strategy.mergeColor(this, color)) {
                this.color(color);
            }
            if (merges.contains((Object)Merge.DECORATIONS)) {
                int length = DECORATIONS.length;
                for (int i = 0; i < length; ++i) {
                    TextDecoration decoration = DECORATIONS[i];
                    TextDecoration.State state = that.decoration(decoration);
                    if (state == TextDecoration.State.NOT_SET || !strategy.mergeDecoration(this, decoration)) continue;
                    this.decoration(decoration, state);
                }
            }
            if (merges.contains((Object)Merge.EVENTS)) {
                HoverEvent hoverEvent;
                ClickEvent clickEvent = that.clickEvent();
                if (clickEvent != null && strategy.mergeClickEvent(this, clickEvent)) {
                    this.clickEvent(clickEvent);
                }
                if ((hoverEvent = that.hoverEvent()) != null && strategy.mergeHoverEvent(this, hoverEvent)) {
                    this.hoverEvent(hoverEvent);
                }
            }
            if (merges.contains((Object)Merge.INSERTION) && (insertion = that.insertion()) != null && strategy.mergeInsertion(this, insertion)) {
                this.insertion(insertion);
            }
            return this;
        }

        public @NonNull Style build() {
            if (this.isEmpty()) {
                return EMPTY;
            }
            return new Style(this.color, this.obfuscated, this.bold, this.strikethrough, this.underlined, this.italic, this.clickEvent, this.hoverEvent, this.insertion);
        }

        private boolean isEmpty() {
            return this.color == null && this.obfuscated == TextDecoration.State.NOT_SET && this.bold == TextDecoration.State.NOT_SET && this.strikethrough == TextDecoration.State.NOT_SET && this.underlined == TextDecoration.State.NOT_SET && this.italic == TextDecoration.State.NOT_SET && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
        }
    }

    public static enum Merge {
        COLOR,
        DECORATIONS,
        EVENTS,
        INSERTION;

        static final Set<Merge> ALL;
        static final Set<Merge> COLOR_AND_DECORATIONS;

        public static @NonNull Set<Merge> all() {
            return ALL;
        }

        public static @NonNull Set<Merge> colorAndDecorations() {
            return COLOR_AND_DECORATIONS;
        }

        public static @NonNull Set<Merge> of(Merge ... merges) {
            return ShadyPines.enumSet(Merge.class, (Enum[])merges);
        }

        static boolean hasAll(@NonNull Set<Merge> merges) {
            return merges.size() == ALL.size();
        }

        static {
            ALL = Merge.of(Merge.values());
            COLOR_AND_DECORATIONS = Merge.of(COLOR, DECORATIONS);
        }

        public static enum Strategy {
            ALWAYS{

                @Override
                boolean mergeColor(@NonNull Builder target, @Nullable TextColor color) {
                    return true;
                }

                @Override
                boolean mergeDecoration(@NonNull Builder target, @NonNull TextDecoration decoration) {
                    return true;
                }

                @Override
                boolean mergeClickEvent(@NonNull Builder target, @Nullable ClickEvent event) {
                    return true;
                }

                @Override
                boolean mergeHoverEvent(@NonNull Builder target, @Nullable HoverEvent event) {
                    return true;
                }

                @Override
                boolean mergeInsertion(@NonNull Builder target, @Nullable String insertion) {
                    return true;
                }
            }
            ,
            NEVER{

                @Override
                boolean mergeColor(@NonNull Builder target, @Nullable TextColor color) {
                    return false;
                }

                @Override
                boolean mergeDecoration(@NonNull Builder target, @NonNull TextDecoration decoration) {
                    return false;
                }

                @Override
                boolean mergeClickEvent(@NonNull Builder target, @Nullable ClickEvent event) {
                    return false;
                }

                @Override
                boolean mergeHoverEvent(@NonNull Builder target, @Nullable HoverEvent event) {
                    return false;
                }

                @Override
                boolean mergeInsertion(@NonNull Builder target, @Nullable String insertion) {
                    return false;
                }
            }
            ,
            IF_ABSENT_ON_TARGET{

                @Override
                boolean mergeColor(@NonNull Builder target, @Nullable TextColor color) {
                    return target.color == null;
                }

                @Override
                boolean mergeDecoration(@NonNull Builder target, @NonNull TextDecoration decoration) {
                    if (decoration == TextDecoration.OBFUSCATED) {
                        return target.obfuscated == TextDecoration.State.NOT_SET;
                    }
                    if (decoration == TextDecoration.BOLD) {
                        return target.bold == TextDecoration.State.NOT_SET;
                    }
                    if (decoration == TextDecoration.STRIKETHROUGH) {
                        return target.strikethrough == TextDecoration.State.NOT_SET;
                    }
                    if (decoration == TextDecoration.UNDERLINED) {
                        return target.underlined == TextDecoration.State.NOT_SET;
                    }
                    if (decoration == TextDecoration.ITALIC) {
                        return target.italic == TextDecoration.State.NOT_SET;
                    }
                    throw new IllegalArgumentException();
                }

                @Override
                boolean mergeClickEvent(@NonNull Builder target, @Nullable ClickEvent event) {
                    return target.clickEvent == null;
                }

                @Override
                boolean mergeHoverEvent(@NonNull Builder target, @Nullable HoverEvent event) {
                    return target.hoverEvent == null;
                }

                @Override
                boolean mergeInsertion(@NonNull Builder target, @Nullable String insertion) {
                    return target.insertion == null;
                }
            };


            abstract boolean mergeColor(@NonNull Builder var1, @Nullable TextColor var2);

            abstract boolean mergeDecoration(@NonNull Builder var1, @NonNull TextDecoration var2);

            abstract boolean mergeClickEvent(@NonNull Builder var1, @Nullable ClickEvent var2);

            abstract boolean mergeHoverEvent(@NonNull Builder var1, @Nullable HoverEvent var2);

            abstract boolean mergeInsertion(@NonNull Builder var1, @Nullable String var2);
        }
    }
}

