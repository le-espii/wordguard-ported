/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.AbstractComponent;
import com.sk89q.worldedit.util.formatting.text.BuildableComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class AbstractComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>>
implements ComponentBuilder<C, B> {
    protected List<Component> children = AbstractComponent.EMPTY_COMPONENT_LIST;
    private @Nullable Style style;
    private @Nullable Style.Builder styleBuilder;

    static <C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> C configureAndBuild(B builder, Consumer<? super B> consumer) {
        consumer.accept(builder);
        return builder.build();
    }

    protected AbstractComponentBuilder() {
    }

    protected AbstractComponentBuilder(@NonNull C component) {
        List<Component> children = component.children();
        if (!children.isEmpty()) {
            this.children = new ArrayList<Component>(children);
        }
        if (component.hasStyling()) {
            this.style = component.style();
        }
    }

    @Override
    public @NonNull B append(@NonNull Component component) {
        this.prepareChildren();
        this.children.add(component);
        return (B)this;
    }

    @Override
    public @NonNull B append(Component ... components) {
        this.prepareChildren();
        Collections.addAll(this.children, components);
        return (B)this;
    }

    @Override
    public @NonNull B append(@NonNull Iterable<? extends Component> components) {
        this.prepareChildren();
        components.forEach(this.children::add);
        return (B)this;
    }

    private void prepareChildren() {
        if (this.children == AbstractComponent.EMPTY_COMPONENT_LIST) {
            this.children = new ArrayList<Component>();
        }
    }

    @Override
    public @NonNull B applyDeep(@NonNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
        this.apply(consumer);
        if (this.children == AbstractComponent.EMPTY_COMPONENT_LIST) {
            return (B)this;
        }
        ListIterator<Component> it = this.children.listIterator();
        while (it.hasNext()) {
            Component child = it.next();
            if (!(child instanceof BuildableComponent)) continue;
            Object childBuilder = ((BuildableComponent)child).toBuilder();
            childBuilder.applyDeep(consumer);
            it.set((Component)childBuilder.build());
        }
        return (B)this;
    }

    @Override
    public @NonNull B mapChildren(@NonNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> function) {
        if (this.children == AbstractComponent.EMPTY_COMPONENT_LIST) {
            return (B)this;
        }
        ListIterator<Component> it = this.children.listIterator();
        while (it.hasNext()) {
            BuildableComponent<?, ?> mappedChild;
            Component child = it.next();
            if (!(child instanceof BuildableComponent) || child == (mappedChild = function.apply((BuildableComponent)child))) continue;
            it.set(mappedChild);
        }
        return (B)this;
    }

    @Override
    public @NonNull B mapChildrenDeep(@NonNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> function) {
        if (this.children == AbstractComponent.EMPTY_COMPONENT_LIST) {
            return (B)this;
        }
        ListIterator<Component> it = this.children.listIterator();
        while (it.hasNext()) {
            Component child = it.next();
            if (!(child instanceof BuildableComponent)) continue;
            BuildableComponent<?, ?> mappedChild = function.apply((BuildableComponent)child);
            if (mappedChild.children().isEmpty()) {
                if (child == mappedChild) continue;
                it.set(mappedChild);
                continue;
            }
            Object builder = mappedChild.toBuilder();
            builder.mapChildrenDeep(function);
            it.set((Component)builder.build());
        }
        return (B)this;
    }

    @Override
    public @NonNull B style(@NonNull Style style) {
        this.style = style;
        this.styleBuilder = null;
        return (B)this;
    }

    @Override
    public @NonNull B style(@NonNull Consumer<Style.Builder> consumer) {
        consumer.accept(this.styleBuilder());
        return (B)this;
    }

    @Override
    public @NonNull B color(@Nullable TextColor color) {
        this.styleBuilder().color(color);
        return (B)this;
    }

    @Override
    public @NonNull B colorIfAbsent(@Nullable TextColor color) {
        this.styleBuilder().colorIfAbsent(color);
        return (B)this;
    }

    @Override
    public @NonNull B decoration(@NonNull TextDecoration decoration, @NonNull TextDecoration.State state) {
        this.styleBuilder().decoration(decoration, state);
        return (B)this;
    }

    @Override
    public @NonNull B clickEvent(@Nullable ClickEvent event) {
        this.styleBuilder().clickEvent(event);
        return (B)this;
    }

    @Override
    public @NonNull B hoverEvent(@Nullable HoverEvent event) {
        this.styleBuilder().hoverEvent(event);
        return (B)this;
    }

    @Override
    public @NonNull B insertion(@Nullable String insertion) {
        this.styleBuilder().insertion(insertion);
        return (B)this;
    }

    @Override
    public @NonNull B mergeStyle(@NonNull Component that, @NonNull Set<Style.Merge> merges) {
        this.styleBuilder().merge(that.style(), merges);
        return (B)this;
    }

    @Override
    public @NonNull B resetStyle() {
        this.style = null;
        this.styleBuilder = null;
        return (B)this;
    }

    private @NonNull Style.Builder styleBuilder() {
        if (this.styleBuilder == null) {
            if (this.style != null) {
                this.styleBuilder = this.style.toBuilder();
                this.style = null;
            } else {
                this.styleBuilder = Style.builder();
            }
        }
        return this.styleBuilder;
    }

    protected final boolean hasStyle() {
        return this.styleBuilder != null || this.style != null;
    }

    protected @NonNull Style buildStyle() {
        if (this.styleBuilder != null) {
            return this.styleBuilder.build();
        }
        if (this.style != null) {
            return this.style;
        }
        return Style.empty();
    }
}

