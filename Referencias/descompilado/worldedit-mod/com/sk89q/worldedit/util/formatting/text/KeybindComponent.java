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
import com.sk89q.worldedit.util.formatting.text.KeybindComponentImpl;
import com.sk89q.worldedit.util.formatting.text.ScopedComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface KeybindComponent
extends BuildableComponent<KeybindComponent, Builder>,
ScopedComponent<KeybindComponent> {
    public static @NonNull Builder builder() {
        return new KeybindComponentImpl.BuilderImpl();
    }

    public static @NonNull Builder builder(@NonNull String keybind) {
        return KeybindComponent.builder().keybind(keybind);
    }

    public static @NonNull KeybindComponent of(@NonNull String keybind) {
        return (KeybindComponent)KeybindComponent.builder(keybind).build();
    }

    public static @NonNull KeybindComponent of(@NonNull String keybind, @Nullable TextColor color) {
        return KeybindComponent.of(keybind, color, Collections.emptySet());
    }

    public static @NonNull KeybindComponent of(@NonNull String content, @Nullable TextColor color, TextDecoration ... decorations) {
        return KeybindComponent.of(content, color, ShadyPines.enumSet(TextDecoration.class, (Enum[])decorations));
    }

    public static @NonNull KeybindComponent of(@NonNull String keybind, @Nullable TextColor color, @NonNull Set<TextDecoration> decorations) {
        return (KeybindComponent)((Builder)((Builder)KeybindComponent.builder(keybind).color(color)).decorations(decorations, true)).build();
    }

    public static @NonNull KeybindComponent make(@NonNull Consumer<? super Builder> consumer) {
        Builder builder = KeybindComponent.builder();
        return (KeybindComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public static @NonNull KeybindComponent make(@NonNull String keybind, @NonNull Consumer<? super Builder> consumer) {
        Builder builder = KeybindComponent.builder(keybind);
        return (KeybindComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public @NonNull String keybind();

    public @NonNull KeybindComponent keybind(@NonNull String var1);

    public static interface Builder
    extends ComponentBuilder<KeybindComponent, Builder> {
        public @NonNull Builder keybind(@NonNull String var1);
    }
}

