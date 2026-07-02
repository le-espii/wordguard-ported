/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.renderer;

import com.sk89q.worldedit.util.formatting.text.BlockNbtComponent;
import com.sk89q.worldedit.util.formatting.text.BuildableComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.EntityNbtComponent;
import com.sk89q.worldedit.util.formatting.text.KeybindComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.ScoreComponent;
import com.sk89q.worldedit.util.formatting.text.SelectorComponent;
import com.sk89q.worldedit.util.formatting.text.StorageNbtComponent;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.renderer.AbstractComponentRenderer;
import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.BiFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class TranslatableComponentRenderer<C>
extends AbstractComponentRenderer<C> {
    public static <C> @NonNull TranslatableComponentRenderer<C> from(final @NonNull BiFunction<C, String, MessageFormat> translations) {
        return new TranslatableComponentRenderer<C>(){

            @Override
            protected @Nullable MessageFormat translation(@NonNull C context, @NonNull String key) {
                return (MessageFormat)translations.apply(context, key);
            }
        };
    }

    @Override
    protected @NonNull Component renderBlockNbt(@NonNull BlockNbtComponent component, @NonNull C context) {
        BlockNbtComponent.Builder builder = TranslatableComponentRenderer.nbt(BlockNbtComponent.builder(), component).pos(component.pos());
        return this.deepRender(component, builder, context);
    }

    @Override
    protected @NonNull Component renderEntityNbt(@NonNull EntityNbtComponent component, @NonNull C context) {
        EntityNbtComponent.Builder builder = TranslatableComponentRenderer.nbt(EntityNbtComponent.builder(), component).selector(component.selector());
        return this.deepRender(component, builder, context);
    }

    @Override
    protected @NonNull Component renderStorageNbt(@NonNull StorageNbtComponent component, @NonNull C context) {
        StorageNbtComponent.Builder builder = TranslatableComponentRenderer.nbt(StorageNbtComponent.builder(), component).storage(component.storage());
        return this.deepRender(component, builder, context);
    }

    private static <C extends NbtComponent<C, B>, B extends NbtComponentBuilder<C, B>> B nbt(B builder, C oldComponent) {
        return builder.nbtPath(oldComponent.nbtPath()).interpret(oldComponent.interpret());
    }

    @Override
    protected @NonNull Component renderKeybind(@NonNull KeybindComponent component, @NonNull C context) {
        KeybindComponent.Builder builder = KeybindComponent.builder(component.keybind());
        return this.deepRender(component, builder, context);
    }

    @Override
    protected @NonNull Component renderScore(@NonNull ScoreComponent component, @NonNull C context) {
        ScoreComponent.Builder builder = ScoreComponent.builder().name(component.name()).objective(component.objective()).value(component.value());
        return this.deepRender(component, builder, context);
    }

    @Override
    protected @NonNull Component renderSelector(@NonNull SelectorComponent component, @NonNull C context) {
        SelectorComponent.Builder builder = SelectorComponent.builder(component.pattern());
        return this.deepRender(component, builder, context);
    }

    @Override
    protected @NonNull Component renderText(@NonNull TextComponent component, @NonNull C context) {
        TextComponent.Builder builder = TextComponent.builder(component.content());
        return this.deepRender(component, builder, context);
    }

    @Override
    protected @NonNull Component renderTranslatable(@NonNull TranslatableComponent component, @NonNull C context) {
        MessageFormat format = this.translation(context, component.key());
        if (format == null) {
            return component;
        }
        List<Component> args = component.args();
        TextComponent.Builder builder = TextComponent.builder();
        this.mergeStyle(component, builder, context);
        if (args.isEmpty()) {
            return builder.content(format.format(null, new StringBuffer(), null).toString()).build();
        }
        Object[] nulls = new Object[args.size()];
        StringBuffer sb = format.format(nulls, new StringBuffer(), (FieldPosition)null);
        AttributedCharacterIterator it = format.formatToCharacterIterator(nulls);
        while (it.getIndex() < it.getEndIndex()) {
            int end = it.getRunLimit();
            Integer index = (Integer)it.getAttribute(MessageFormat.Field.ARGUMENT);
            if (index != null) {
                builder.append(this.render(args.get(index), context));
            } else {
                builder.append((Component)TextComponent.of(sb.substring(it.getIndex(), end)));
            }
            it.setIndex(end);
        }
        return this.deepRender(component, builder, context);
    }

    private <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O deepRender(Component component, B builder, C context) {
        this.mergeStyle(component, builder, context);
        component.children().forEach(child -> builder.append(this.render((Component)child, context)));
        return builder.build();
    }

    private <B extends ComponentBuilder<?, ?>> void mergeStyle(Component component, B builder, C context) {
        builder.mergeStyle(component, Style.Merge.colorAndDecorations());
        builder.clickEvent(component.clickEvent());
        HoverEvent hoverEvent = component.hoverEvent();
        if (hoverEvent != null) {
            builder.hoverEvent(HoverEvent.of(hoverEvent.action(), this.render(hoverEvent.value(), context)));
        }
    }

    protected abstract @Nullable MessageFormat translation(@NonNull C var1, @NonNull String var2);
}

