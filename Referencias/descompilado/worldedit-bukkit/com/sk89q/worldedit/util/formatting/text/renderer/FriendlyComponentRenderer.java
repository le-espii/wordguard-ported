/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.renderer;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.renderer.ComponentRenderer;
import com.sk89q.worldedit.util.formatting.text.renderer.TranslatableComponentRenderer;
import java.text.MessageFormat;
import java.util.function.BiFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated
public abstract class FriendlyComponentRenderer<C>
implements ComponentRenderer<C> {
    private final TranslatableComponentRenderer<C> renderer = new TranslatableComponentRenderer<C>(){

        @Override
        protected @Nullable MessageFormat translation(@NonNull C context, @NonNull String key) {
            return FriendlyComponentRenderer.this.translation(context, key);
        }
    };

    public static <C> @NonNull FriendlyComponentRenderer<C> from(final @NonNull BiFunction<C, String, MessageFormat> translations) {
        return new FriendlyComponentRenderer<C>(){

            @Override
            protected @NonNull MessageFormat translation(@NonNull C context, @NonNull String key) {
                return (MessageFormat)translations.apply(context, key);
            }
        };
    }

    @Override
    public @NonNull Component render(@NonNull Component component, @NonNull C context) {
        return this.renderer.render(component, context);
    }

    protected abstract @Nullable MessageFormat translation(@NonNull C var1, @NonNull String var2);
}

