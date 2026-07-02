/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ScopedComponent<C extends Component>
extends Component {
    public @NonNull C children(@NonNull List<Component> var1);

    public @NonNull C style(@NonNull Style var1);

    default public @NonNull C style(@NonNull Consumer<Style.Builder> style) {
        return (C)Component.super.style(style);
    }

    default public @NonNull C style(@NonNull Style.Builder style) {
        return (C)Component.super.style(style);
    }

    default public @NonNull C mergeStyle(@NonNull Component that) {
        return (C)Component.super.mergeStyle(that);
    }

    default public @NonNull C mergeStyle(@NonNull Component that, Style.Merge ... merges) {
        return (C)Component.super.mergeStyle(that, merges);
    }

    default public @NonNull C append(@NonNull Component component) {
        this.detectCycle(component);
        List<Component> oldChildren = this.children();
        ArrayList<Component> newChildren = new ArrayList<Component>(oldChildren.size() + 1);
        newChildren.addAll(oldChildren);
        newChildren.add(component);
        return this.children(newChildren);
    }

    default public @NonNull C append(@NonNull ComponentBuilder<?, ?> builder) {
        return (C)Component.super.append(builder);
    }

    default public @NonNull C mergeStyle(@NonNull Component that, @NonNull Set<Style.Merge> merges) {
        return (C)Component.super.mergeStyle(that, merges);
    }

    default public @NonNull C color(@Nullable TextColor color) {
        return (C)Component.super.color(color);
    }

    default public @NonNull C colorIfAbsent(@Nullable TextColor color) {
        return (C)Component.super.colorIfAbsent(color);
    }

    default public @NonNull C decoration(@NonNull TextDecoration decoration, boolean flag) {
        return (C)Component.super.decoration(decoration, flag);
    }

    default public @NonNull C decoration(@NonNull TextDecoration decoration, @NonNull TextDecoration.State state) {
        return (C)Component.super.decoration(decoration, state);
    }

    default public @NonNull C clickEvent(@Nullable ClickEvent event) {
        return (C)Component.super.clickEvent(event);
    }

    default public @NonNull C hoverEvent(@Nullable HoverEvent event) {
        return (C)Component.super.hoverEvent(event);
    }

    default public @NonNull C insertion(@Nullable String insertion) {
        return (C)Component.super.insertion(insertion);
    }

    @Deprecated
    default public @NonNull C mergeColor(@NonNull Component that) {
        return (C)Component.super.mergeColor(that);
    }

    @Deprecated
    default public @NonNull C mergeDecorations(@NonNull Component that) {
        return (C)Component.super.mergeDecorations(that);
    }

    @Deprecated
    default public @NonNull C mergeEvents(@NonNull Component that) {
        return (C)Component.super.mergeEvents(that);
    }
}

