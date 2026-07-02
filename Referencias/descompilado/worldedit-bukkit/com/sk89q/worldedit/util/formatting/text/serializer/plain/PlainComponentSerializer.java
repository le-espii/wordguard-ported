/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.serializer.plain;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.KeybindComponent;
import com.sk89q.worldedit.util.formatting.text.ScoreComponent;
import com.sk89q.worldedit.util.formatting.text.SelectorComponent;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.serializer.ComponentSerializer;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PlainComponentSerializer
implements ComponentSerializer<Component, TextComponent, String> {
    public static final PlainComponentSerializer INSTANCE = new PlainComponentSerializer();
    private final Function<KeybindComponent, String> keybind;
    private final Function<TranslatableComponent, String> translatable;

    public PlainComponentSerializer() {
        this(component -> "", component -> "");
    }

    public PlainComponentSerializer(@NonNull Function<KeybindComponent, String> keybind, @NonNull Function<TranslatableComponent, String> translatable) {
        this.keybind = keybind;
        this.translatable = translatable;
    }

    @Override
    public @NonNull TextComponent deserialize(@NonNull String input) {
        return TextComponent.of(input);
    }

    @Override
    public @NonNull String serialize(@NonNull Component component) {
        StringBuilder sb = new StringBuilder();
        this.serialize(sb, component);
        return sb.toString();
    }

    public void serialize(@NonNull StringBuilder sb, @NonNull Component component) {
        if (component instanceof KeybindComponent) {
            sb.append(this.keybind.apply((KeybindComponent)component));
        } else if (component instanceof ScoreComponent) {
            sb.append(((ScoreComponent)component).value());
        } else if (component instanceof SelectorComponent) {
            sb.append(((SelectorComponent)component).pattern());
        } else if (component instanceof TextComponent) {
            sb.append(((TextComponent)component).content());
        } else if (component instanceof TranslatableComponent) {
            sb.append(this.translatable.apply((TranslatableComponent)component));
        } else {
            throw new IllegalArgumentException("Don't know how to turn " + component + " into a string");
        }
        for (Component child : component.children()) {
            this.serialize(sb, child);
        }
    }
}

