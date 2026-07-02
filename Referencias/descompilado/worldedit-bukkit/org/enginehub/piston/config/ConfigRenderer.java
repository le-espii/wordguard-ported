/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package org.enginehub.piston.config;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.renderer.ComponentRenderer;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.enginehub.piston.config.Config;
import org.enginehub.piston.config.ConfigHolder;

public class ConfigRenderer
implements ComponentRenderer<ConfigHolder> {
    private static final ConfigRenderer INSTANCE = new ConfigRenderer();

    public static ConfigRenderer getInstance() {
        return INSTANCE;
    }

    private ConfigRenderer() {
    }

    @Override
    public @NonNull Component render(@NonNull Component component, @NonNull ConfigHolder context) {
        if ((component = this.replaceSubcomponents(component, context)) instanceof TranslatableComponent) {
            TranslatableComponent tc = (TranslatableComponent)component;
            Config<?> config = context.getConfigs().get(tc.key());
            if (config != null) {
                component = config.apply(tc);
            }
        }
        return component;
    }

    private Component replaceSubcomponents(Component component, ConfigHolder context) {
        List<Component> replacement;
        List<Component> original;
        List<Component> replacementArgs;
        TranslatableComponent tc;
        List<Component> originalArgs;
        if (component instanceof TranslatableComponent && (originalArgs = (tc = (TranslatableComponent)component).args()) != (replacementArgs = this.renderList(originalArgs, context))) {
            component = tc.args(replacementArgs);
        }
        return (original = component.children()) != (replacement = this.renderList(original, context)) ? component.children(replacement) : component;
    }

    private List<Component> renderList(List<Component> input, ConfigHolder context) {
        if (input.isEmpty()) {
            return input;
        }
        ImmutableList.Builder copy = ImmutableList.builder();
        boolean modified = false;
        for (Component component : input) {
            Component replacement = this.render(component, context);
            if (replacement != component) {
                modified = true;
            }
            copy.add((Object)replacement);
        }
        return modified ? copy.build() : input;
    }
}

