/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.AbstractComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.BuildableComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.ScopedComponent;
import com.sk89q.worldedit.util.formatting.text.TextComponentImpl;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface TextComponent
extends BuildableComponent<TextComponent, Builder>,
ScopedComponent<TextComponent> {
    public static @NonNull TextComponent empty() {
        return TextComponentImpl.EMPTY;
    }

    public static @NonNull TextComponent newline() {
        return TextComponentImpl.NEWLINE;
    }

    public static @NonNull TextComponent space() {
        return TextComponentImpl.SPACE;
    }

    public static @NonNull TextComponent join(@NonNull Component separator, Component ... components) {
        return TextComponent.join(separator, Arrays.asList(components));
    }

    public static @NonNull TextComponent join(@NonNull Component separator, Iterable<? extends Component> components) {
        Iterator<? extends Component> it = components.iterator();
        if (!it.hasNext()) {
            return TextComponent.empty();
        }
        Builder builder = TextComponent.builder();
        while (it.hasNext()) {
            builder.append(it.next());
            if (!it.hasNext()) continue;
            builder.append(separator);
        }
        return (TextComponent)builder.build();
    }

    public static @NonNull TextComponent of(@NonNull String content) {
        if (content.isEmpty()) {
            return TextComponent.empty();
        }
        return (TextComponent)TextComponent.builder(content).build();
    }

    public static @NonNull TextComponent of(@NonNull String content, @Nullable TextColor color) {
        return (TextComponent)((Builder)TextComponent.builder(content).color(color)).build();
    }

    public static @NonNull TextComponent of(@NonNull String content, @Nullable TextColor color, TextDecoration ... decorations) {
        return TextComponent.of(content, color, (Set<TextDecoration>)ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull TextComponent of(@NonNull String content, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (TextComponent)((Builder)((Builder)TextComponent.builder(content).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull TextComponent of(@NonNull String content, @NonNull Style style) {
        return (TextComponent)((Builder)TextComponent.builder(content).style(style)).build();
    }

    public static @NonNull TextComponent of(boolean value) {
        return TextComponent.of(String.valueOf(value));
    }

    public static @NonNull TextComponent of(boolean value, @Nullable TextColor color) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).build();
    }

    public static @NonNull TextComponent of(boolean value, @Nullable TextColor color, TextDecoration ... decorations) {
        return TextComponent.of(String.valueOf(value), color, (Set<TextDecoration>)ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull TextComponent of(boolean value, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (TextComponent)((Builder)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull TextComponent of(boolean value, @NonNull Style style) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).style(style)).build();
    }

    public static @NonNull TextComponent of(char value) {
        if (value == '\n') {
            return TextComponent.newline();
        }
        if (value == ' ') {
            return TextComponent.space();
        }
        return TextComponent.of(String.valueOf(value));
    }

    public static @NonNull TextComponent of(char value, @Nullable TextColor color) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).build();
    }

    public static @NonNull TextComponent of(char value, @Nullable TextColor color, TextDecoration ... decorations) {
        return TextComponent.of(String.valueOf(value), color, (Set<TextDecoration>)ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull TextComponent of(char value, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (TextComponent)((Builder)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull TextComponent of(char value, @NonNull Style style) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).style(style)).build();
    }

    public static @NonNull TextComponent of(double value) {
        return TextComponent.of(String.valueOf(value));
    }

    public static @NonNull TextComponent of(double value, @Nullable TextColor color) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).build();
    }

    public static @NonNull TextComponent of(double value, @Nullable TextColor color, TextDecoration ... decorations) {
        return TextComponent.of(String.valueOf(value), color, (Set<TextDecoration>)ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull TextComponent of(double value, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (TextComponent)((Builder)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull TextComponent of(double value, @NonNull Style style) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).style(style)).build();
    }

    public static @NonNull TextComponent of(float value) {
        return TextComponent.of(String.valueOf(value));
    }

    public static @NonNull TextComponent of(float value, @Nullable TextColor color) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).build();
    }

    public static @NonNull TextComponent of(float value, @Nullable TextColor color, TextDecoration ... decorations) {
        return TextComponent.of(String.valueOf(value), color, (Set<TextDecoration>)ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull TextComponent of(float value, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (TextComponent)((Builder)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull TextComponent of(float value, @NonNull Style style) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).style(style)).build();
    }

    public static @NonNull TextComponent of(int value) {
        return TextComponent.of(String.valueOf(value));
    }

    public static @NonNull TextComponent of(int value, @Nullable TextColor color) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).build();
    }

    public static @NonNull TextComponent of(int value, @Nullable TextColor color, TextDecoration ... decorations) {
        return TextComponent.of(String.valueOf(value), color, (Set<TextDecoration>)ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull TextComponent of(int value, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (TextComponent)((Builder)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull TextComponent of(int value, @NonNull Style style) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).style(style)).build();
    }

    public static @NonNull TextComponent of(long value) {
        return TextComponent.of(String.valueOf(value));
    }

    public static @NonNull TextComponent of(long value, @Nullable TextColor color) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).build();
    }

    public static @NonNull TextComponent of(long value, @Nullable TextColor color, TextDecoration ... decorations) {
        return TextComponent.of(String.valueOf(value), color, (Set<TextDecoration>)ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull TextComponent of(long value, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (TextComponent)((Builder)((Builder)TextComponent.builder(String.valueOf(value)).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull TextComponent of(long value, @NonNull Style style) {
        return (TextComponent)((Builder)TextComponent.builder(String.valueOf(value)).style(style)).build();
    }

    public static @NonNull Builder builder() {
        return new TextComponentImpl.BuilderImpl();
    }

    public static @NonNull Builder builder(@NonNull String content) {
        return TextComponent.builder().content(content);
    }

    public static @NonNull Builder builder(@NonNull String content, @Nullable TextColor color) {
        return (Builder)TextComponent.builder(content).color(color);
    }

    public static @NonNull TextComponent make(@NonNull Consumer<? super Builder> consumer) {
        Builder builder = TextComponent.builder();
        return (TextComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public static @NonNull TextComponent make(@NonNull String content, @NonNull Consumer<? super Builder> consumer) {
        Builder builder = TextComponent.builder(content);
        return (TextComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public @NonNull String content();

    public @NonNull TextComponent content(@NonNull String var1);

    public boolean isEmpty();

    public static interface Builder
    extends ComponentBuilder<TextComponent, Builder> {
        public @NonNull Builder content(@NonNull String var1);
    }
}

