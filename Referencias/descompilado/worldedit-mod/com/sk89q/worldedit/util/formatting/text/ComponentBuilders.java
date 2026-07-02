/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.BlockNbtComponent;
import com.sk89q.worldedit.util.formatting.text.EntityNbtComponent;
import com.sk89q.worldedit.util.formatting.text.KeybindComponent;
import com.sk89q.worldedit.util.formatting.text.ScoreComponent;
import com.sk89q.worldedit.util.formatting.text.SelectorComponent;
import com.sk89q.worldedit.util.formatting.text.StorageNbtComponent;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ComponentBuilders {
    public static @NonNull BlockNbtComponent.Builder blockNbt() {
        return BlockNbtComponent.builder();
    }

    public static @NonNull EntityNbtComponent.Builder entityNbt() {
        return EntityNbtComponent.builder();
    }

    public static @NonNull StorageNbtComponent.Builder storageNbt() {
        return StorageNbtComponent.builder();
    }

    public static @NonNull KeybindComponent.Builder keybind() {
        return KeybindComponent.builder();
    }

    public static @NonNull KeybindComponent.Builder keybind(@NonNull String keybind) {
        return KeybindComponent.builder(keybind);
    }

    public static @NonNull ScoreComponent.Builder score() {
        return ScoreComponent.builder();
    }

    public static @NonNull ScoreComponent.Builder score(@NonNull String name, @NonNull String objective) {
        return ScoreComponent.builder(name, objective);
    }

    public static @NonNull SelectorComponent.Builder selector() {
        return SelectorComponent.builder();
    }

    public static @NonNull SelectorComponent.Builder selector(@NonNull String pattern) {
        return SelectorComponent.builder(pattern);
    }

    public static @NonNull TextComponent.Builder text() {
        return TextComponent.builder();
    }

    public static @NonNull TextComponent.Builder text(@NonNull String content, @Nullable TextColor color) {
        return (TextComponent.Builder)TextComponent.builder(content).color(color);
    }

    public static @NonNull TextComponent.Builder text(char value) {
        return ComponentBuilders.text(String.valueOf(value));
    }

    public static @NonNull TextComponent.Builder text(double value) {
        return ComponentBuilders.text(String.valueOf(value));
    }

    public static @NonNull TextComponent.Builder text(float value) {
        return ComponentBuilders.text(String.valueOf(value));
    }

    public static @NonNull TextComponent.Builder text(int value) {
        return ComponentBuilders.text(String.valueOf(value));
    }

    public static @NonNull TextComponent.Builder text(long value) {
        return ComponentBuilders.text(String.valueOf(value));
    }

    public static @NonNull TextComponent.Builder text(@NonNull String content) {
        return TextComponent.builder(content);
    }

    public static @NonNull TranslatableComponent.Builder translatable() {
        return TranslatableComponent.builder();
    }

    public static @NonNull TranslatableComponent.Builder translatable(@NonNull String key) {
        return TranslatableComponent.builder(key);
    }
}

