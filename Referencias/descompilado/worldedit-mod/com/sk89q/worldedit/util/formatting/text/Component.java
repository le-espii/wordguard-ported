/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Component {
    public @NonNull List<Component> children();

    public @NonNull Component children(@NonNull List<Component> var1);

    default public boolean contains(@NonNull Component that) {
        if (this == that) {
            return true;
        }
        for (Component child : this.children()) {
            if (!child.contains(that)) continue;
            return true;
        }
        HoverEvent hoverEvent = this.hoverEvent();
        if (hoverEvent != null) {
            Component hover = hoverEvent.value();
            if (that == hover) {
                return true;
            }
            for (Component child : hover.children()) {
                if (!child.contains(that)) continue;
                return true;
            }
        }
        return false;
    }

    default public void detectCycle(@NonNull Component that) {
        if (that.contains(this)) {
            throw new IllegalStateException("Component cycle detected between " + this + " and " + that);
        }
    }

    public @NonNull Component append(@NonNull Component var1);

    default public @NonNull Component append(@NonNull ComponentBuilder<?, ?> builder) {
        return this.append((Component)builder.build());
    }

    public @NonNull Style style();

    public @NonNull Component style(@NonNull Style var1);

    default public @NonNull Component style(@NonNull Consumer<Style.Builder> consumer) {
        return this.style(this.style().edit(consumer));
    }

    default public @NonNull Component style(@NonNull Consumer<Style.Builder> consumer, @NonNull Style.Merge.Strategy strategy) {
        return this.style(this.style().edit(consumer, strategy));
    }

    default public @NonNull Component style(@NonNull Style.Builder style) {
        return this.style(style.build());
    }

    default public @NonNull Component mergeStyle(@NonNull Component that) {
        return this.mergeStyle(that, Style.Merge.all());
    }

    default public @NonNull Component mergeStyle(@NonNull Component that, Style.Merge ... merges) {
        return this.mergeStyle(that, Style.Merge.of(merges));
    }

    default public @NonNull Component mergeStyle(@NonNull Component that, @NonNull Set<Style.Merge> merges) {
        return this.style(this.style().merge(that.style(), merges));
    }

    default public @Nullable TextColor color() {
        return this.style().color();
    }

    default public @NonNull Component color(@Nullable TextColor color) {
        return this.style(this.style().color(color));
    }

    default public @NonNull Component colorIfAbsent(@Nullable TextColor color) {
        if (this.color() == null) {
            return this.color(color);
        }
        return this;
    }

    default public boolean hasDecoration(@NonNull TextDecoration decoration) {
        return this.decoration(decoration) == TextDecoration.State.TRUE;
    }

    default public @NonNull TextDecoration.State decoration(@NonNull TextDecoration decoration) {
        return this.style().decoration(decoration);
    }

    default public @NonNull Component decoration(@NonNull TextDecoration decoration, boolean flag) {
        return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
    }

    default public @NonNull Component decoration(@NonNull TextDecoration decoration, @NonNull TextDecoration.State state) {
        return this.style(this.style().decoration(decoration, state));
    }

    default public @NonNull Set<TextDecoration> decorations() {
        return this.decorations(Collections.emptySet());
    }

    default public @NonNull Set<TextDecoration> decorations(@NonNull Set<TextDecoration> defaultValues) {
        return this.style().decorations(defaultValues);
    }

    default public @Nullable ClickEvent clickEvent() {
        return this.style().clickEvent();
    }

    default public @NonNull Component clickEvent(@Nullable ClickEvent event) {
        return this.style(this.style().clickEvent(event));
    }

    default public @Nullable HoverEvent hoverEvent() {
        return this.style().hoverEvent();
    }

    default public @NonNull Component hoverEvent(@Nullable HoverEvent event) {
        if (event != null) {
            this.detectCycle(event.value());
        }
        return this.style(this.style().hoverEvent(event));
    }

    default public @Nullable String insertion() {
        return this.style().insertion();
    }

    default public @NonNull Component insertion(@Nullable String insertion) {
        return this.style(this.style().insertion(insertion));
    }

    @Deprecated
    default public @NonNull Component mergeColor(@NonNull Component that) {
        return this.style(this.style().mergeColor(that.style()));
    }

    @Deprecated
    default public @NonNull Component mergeDecorations(@NonNull Component that) {
        return this.style(this.style().mergeDecorations(that.style()));
    }

    @Deprecated
    default public @NonNull Component mergeEvents(@NonNull Component that) {
        return this.style(this.style().mergeEvents(that.style()));
    }

    default public boolean hasStyling() {
        return !this.style().isEmpty();
    }
}

