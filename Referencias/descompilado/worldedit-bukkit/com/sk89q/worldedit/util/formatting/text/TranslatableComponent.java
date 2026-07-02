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
import com.sk89q.worldedit.util.formatting.text.TranslatableComponentImpl;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface TranslatableComponent
extends BuildableComponent<TranslatableComponent, Builder>,
ScopedComponent<TranslatableComponent> {
    public static @NonNull Builder builder() {
        return new TranslatableComponentImpl.BuilderImpl();
    }

    public static @NonNull Builder builder(@NonNull String key) {
        return TranslatableComponent.builder().key(key);
    }

    public static @NonNull TranslatableComponent of(@NonNull String key) {
        return (TranslatableComponent)TranslatableComponent.builder(key).build();
    }

    public static @NonNull TranslatableComponent of(@NonNull String key, @Nullable TextColor color) {
        return (TranslatableComponent)((Builder)TranslatableComponent.builder(key).color(color)).build();
    }

    public static @NonNull TranslatableComponent of(@NonNull String key, @Nullable TextColor color, TextDecoration ... decorations) {
        return TranslatableComponent.of(key, color, ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull TranslatableComponent of(@NonNull String key, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (TranslatableComponent)((Builder)((Builder)TranslatableComponent.builder(key).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull TranslatableComponent of(@NonNull String key, Component ... args) {
        return TranslatableComponent.of(key, null, args);
    }

    public static @NonNull TranslatableComponent of(@NonNull String key, @Nullable TextColor color, Component ... args) {
        return TranslatableComponent.of(key, color, Collections.emptySet(), args);
    }

    public static @NonNull TranslatableComponent of(@NonNull String key, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations, Component ... args) {
        return TranslatableComponent.of(key, color, decorations, Arrays.asList(args));
    }

    public static @NonNull TranslatableComponent of(@NonNull String key, @NonNull List<? extends Component> args) {
        return TranslatableComponent.of(key, null, args);
    }

    public static TranslatableComponent of(@NonNull String key, @Nullable TextColor color, @NonNull List<? extends Component> args) {
        return TranslatableComponent.of(key, color, Collections.emptySet(), args);
    }

    public static @NonNull TranslatableComponent of(@NonNull String key, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations, @NonNull List<? extends Component> args) {
        return (TranslatableComponent)((Builder)((Builder)TranslatableComponent.builder(key).color(color)).decorations(decorations, true)).args(args).build();
    }

    public static @NonNull TranslatableComponent make(@NonNull Consumer<? super Builder> consumer) {
        Builder builder = TranslatableComponent.builder();
        return (TranslatableComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public static @NonNull TranslatableComponent make(@NonNull String key, @NonNull Consumer<? super Builder> consumer) {
        Builder builder = TranslatableComponent.builder(key);
        return (TranslatableComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public static @NonNull TranslatableComponent make(@NonNull String key, @NonNull List<? extends Component> args, @NonNull Consumer<? super Builder> consumer) {
        Builder builder = TranslatableComponent.builder(key).args(args);
        return (TranslatableComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public @NonNull String key();

    public @NonNull TranslatableComponent key(@NonNull String var1);

    public @NonNull List<Component> args();

    public @NonNull TranslatableComponent args(Component ... var1);

    public @NonNull TranslatableComponent args(@NonNull List<? extends Component> var1);

    public static interface Builder
    extends ComponentBuilder<TranslatableComponent, Builder> {
        public @NonNull Builder key(@NonNull String var1);

        public @NonNull Builder args(@NonNull ComponentBuilder<?, ?> var1);

        public @NonNull Builder args(ComponentBuilder<?, ?> ... var1);

        public @NonNull Builder args(@NonNull Component var1);

        public @NonNull Builder args(Component ... var1);

        public @NonNull Builder args(@NonNull List<? extends Component> var1);
    }
}

