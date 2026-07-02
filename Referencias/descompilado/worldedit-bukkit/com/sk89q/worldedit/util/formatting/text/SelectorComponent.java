/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.AbstractComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.BuildableComponent;
import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.ScopedComponent;
import com.sk89q.worldedit.util.formatting.text.SelectorComponentImpl;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface SelectorComponent
extends BuildableComponent<SelectorComponent, Builder>,
ScopedComponent<SelectorComponent> {
    public static @NonNull Builder builder() {
        return new SelectorComponentImpl.BuilderImpl();
    }

    public static @NonNull Builder builder(@NonNull String pattern) {
        return SelectorComponent.builder().pattern(pattern);
    }

    public static @NonNull SelectorComponent of(@NonNull String pattern) {
        return (SelectorComponent)SelectorComponent.builder(pattern).build();
    }

    public static @NonNull SelectorComponent make(@NonNull Consumer<? super Builder> consumer) {
        Builder builder = SelectorComponent.builder();
        return (SelectorComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public static @NonNull SelectorComponent make(@NonNull String pattern, @NonNull Consumer<? super Builder> consumer) {
        Builder builder = SelectorComponent.builder(pattern);
        return (SelectorComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public @NonNull String pattern();

    public @NonNull SelectorComponent pattern(@NonNull String var1);

    public static interface Builder
    extends ComponentBuilder<SelectorComponent, Builder> {
        public @NonNull Builder pattern(@NonNull String var1);
    }
}

