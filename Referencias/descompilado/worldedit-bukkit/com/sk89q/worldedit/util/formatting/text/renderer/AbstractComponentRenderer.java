/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.renderer;

import com.sk89q.worldedit.util.formatting.text.BlockNbtComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.EntityNbtComponent;
import com.sk89q.worldedit.util.formatting.text.KeybindComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponent;
import com.sk89q.worldedit.util.formatting.text.ScoreComponent;
import com.sk89q.worldedit.util.formatting.text.SelectorComponent;
import com.sk89q.worldedit.util.formatting.text.StorageNbtComponent;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.renderer.ComponentRenderer;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractComponentRenderer<C>
implements ComponentRenderer<C> {
    @Override
    public @NonNull Component render(@NonNull Component component, @NonNull C context) {
        if (component instanceof TextComponent) {
            return this.renderText((TextComponent)component, context);
        }
        if (component instanceof TranslatableComponent) {
            return this.renderTranslatable((TranslatableComponent)component, context);
        }
        if (component instanceof KeybindComponent) {
            return this.renderKeybind((KeybindComponent)component, context);
        }
        if (component instanceof ScoreComponent) {
            return this.renderScore((ScoreComponent)component, context);
        }
        if (component instanceof SelectorComponent) {
            return this.renderSelector((SelectorComponent)component, context);
        }
        if (component instanceof NbtComponent) {
            if (component instanceof BlockNbtComponent) {
                return this.renderBlockNbt((BlockNbtComponent)component, context);
            }
            if (component instanceof EntityNbtComponent) {
                return this.renderEntityNbt((EntityNbtComponent)component, context);
            }
            if (component instanceof StorageNbtComponent) {
                return this.renderStorageNbt((StorageNbtComponent)component, context);
            }
        }
        return component;
    }

    protected abstract @NonNull Component renderBlockNbt(@NonNull BlockNbtComponent var1, @NonNull C var2);

    protected abstract @NonNull Component renderEntityNbt(@NonNull EntityNbtComponent var1, @NonNull C var2);

    protected abstract @NonNull Component renderStorageNbt(@NonNull StorageNbtComponent var1, @NonNull C var2);

    protected abstract @NonNull Component renderKeybind(@NonNull KeybindComponent var1, @NonNull C var2);

    protected abstract @NonNull Component renderScore(@NonNull ScoreComponent var1, @NonNull C var2);

    protected abstract @NonNull Component renderSelector(@NonNull SelectorComponent var1, @NonNull C var2);

    protected abstract @NonNull Component renderText(@NonNull TextComponent var1, @NonNull C var2);

    protected abstract @NonNull Component renderTranslatable(@NonNull TranslatableComponent var1, @NonNull C var2);
}

