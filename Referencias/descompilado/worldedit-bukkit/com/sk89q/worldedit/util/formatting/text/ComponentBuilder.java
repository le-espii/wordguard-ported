/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.BuildableComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> {
    default public @NonNull B append(@NonNull String content) {
        return this.append((Component)TextComponent.of(content));
    }

    default public @NonNull B append(@NonNull String content, @NonNull TextColor color) {
        return this.append((Component)TextComponent.of(content, color));
    }

    default public @NonNull B append(@NonNull String content, @NonNull TextColor color, TextDecoration ... decorations) {
        return this.append((Component)TextComponent.of(content, color, decorations));
    }

    default public @NonNull B append(@NonNull String content, @NonNull Consumer<? super TextComponent.Builder> builder) {
        return this.append((Component)TextComponent.make(content, builder));
    }

    public @NonNull B append(@NonNull Component var1);

    default public @NonNull B append(@NonNull ComponentBuilder<?, ?> builder) {
        return this.append((Component)builder.build());
    }

    public @NonNull B append(Component ... var1);

    public @NonNull B append(@NonNull Iterable<? extends Component> var1);

    default public @NonNull B apply(@NonNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
        consumer.accept(this);
        return (B)this;
    }

    public @NonNull B applyDeep(@NonNull Consumer<? super ComponentBuilder<?, ?>> var1);

    public @NonNull B mapChildren(@NonNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> var1);

    public @NonNull B mapChildrenDeep(@NonNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> var1);

    public @NonNull B style(@NonNull Style var1);

    public @NonNull B style(@NonNull Consumer<Style.Builder> var1);

    public @NonNull B color(@Nullable TextColor var1);

    public @NonNull B colorIfAbsent(@Nullable TextColor var1);

    default public @NonNull B decorations(@NonNull Set<TextDecoration> decorations, boolean flag) {
        TextDecoration.State state = TextDecoration.State.byBoolean(flag);
        decorations.forEach(decoration -> this.decoration((TextDecoration)decoration, state));
        return (B)this;
    }

    default public @NonNull B decoration(@NonNull TextDecoration decoration, boolean flag) {
        return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
    }

    public @NonNull B decoration(@NonNull TextDecoration var1, @NonNull TextDecoration.State var2);

    public @NonNull B clickEvent(@Nullable ClickEvent var1);

    public @NonNull B hoverEvent(@Nullable HoverEvent var1);

    public @NonNull B insertion(@Nullable String var1);

    default public @NonNull B mergeStyle(@NonNull Component that) {
        return this.mergeStyle(that, Style.Merge.all());
    }

    default public @NonNull B mergeStyle(@NonNull Component that, Style.Merge ... merges) {
        return this.mergeStyle(that, Style.Merge.of(merges));
    }

    public @NonNull B mergeStyle(@NonNull Component var1, @NonNull Set<Style.Merge> var2);

    @Deprecated
    default public @NonNull B mergeColor(@NonNull Component that) {
        return this.mergeStyle(that, Collections.singleton(Style.Merge.COLOR));
    }

    @Deprecated
    default public @NonNull B mergeDecorations(@NonNull Component that) {
        return this.mergeStyle(that, Collections.singleton(Style.Merge.DECORATIONS));
    }

    @Deprecated
    default public @NonNull B mergeEvents(@NonNull Component that) {
        return this.mergeStyle(that, Collections.singleton(Style.Merge.EVENTS));
    }

    public @NonNull B resetStyle();

    public @NonNull C build();
}

