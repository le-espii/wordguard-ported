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
import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.ScopedComponent;
import com.sk89q.worldedit.util.formatting.text.ScoreComponentImpl;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ScoreComponent
extends BuildableComponent<ScoreComponent, Builder>,
ScopedComponent<ScoreComponent> {
    public static @NonNull Builder builder() {
        return new ScoreComponentImpl.BuilderImpl();
    }

    public static @NonNull Builder builder(@NonNull String name, @NonNull String objective) {
        return ScoreComponent.builder().name(name).objective(objective);
    }

    public static @NonNull ScoreComponent of(@NonNull String name, @NonNull String objective) {
        return ScoreComponent.of(name, objective, null);
    }

    public static @NonNull ScoreComponent of(@NonNull String name, @NonNull String objective, @Nullable String value) {
        return (ScoreComponent)ScoreComponent.builder().name(name).objective(objective).value(value).build();
    }

    public static @NonNull ScoreComponent make(@NonNull Consumer<? super Builder> consumer) {
        Builder builder = ScoreComponent.builder();
        return (ScoreComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public static @NonNull ScoreComponent make(@NonNull String name, @NonNull String objective, @NonNull Consumer<? super Builder> consumer) {
        Builder builder = ScoreComponent.builder(name, objective);
        return (ScoreComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public @NonNull String name();

    public @NonNull ScoreComponent name(@NonNull String var1);

    public @NonNull String objective();

    public @NonNull ScoreComponent objective(@NonNull String var1);

    public @Nullable String value();

    public @NonNull ScoreComponent value(@Nullable String var1);

    public static interface Builder
    extends ComponentBuilder<ScoreComponent, Builder> {
        public @NonNull Builder name(@NonNull String var1);

        public @NonNull Builder objective(@NonNull String var1);

        public @NonNull Builder value(@Nullable String var1);
    }
}

