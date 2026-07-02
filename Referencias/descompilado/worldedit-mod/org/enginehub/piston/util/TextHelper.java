/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 */
package org.enginehub.piston.util;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.KeybindComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.serializer.plain.PlainComponentSerializer;
import java.util.Iterator;
import java.util.List;
import org.enginehub.piston.config.ConfigHolder;
import org.enginehub.piston.config.ConfigRenderer;

public class TextHelper {
    private static final PlainComponentSerializer PLAIN_COMPONENT_SERIALIZER = new PlainComponentSerializer(KeybindComponent::keybind, translatableComponent -> {
        StringBuilder builder = new StringBuilder();
        TextHelper.appendTranslatableTo(builder, translatableComponent);
        return builder.toString();
    });
    private static final ConfigHolder CONFIG = ConfigHolder.create();

    public static String reduceToText(Component component) {
        StringBuilder text = new StringBuilder();
        TextHelper.appendTextTo(text, ConfigRenderer.getInstance().render(component, CONFIG));
        return text.toString();
    }

    private static void appendTextTo(StringBuilder builder, Component component) {
        PLAIN_COMPONENT_SERIALIZER.serialize(builder, component);
    }

    private static void appendTranslatableTo(StringBuilder builder, TranslatableComponent component) {
        builder.append(component.key());
        List<Component> args = component.args();
        if (args.size() > 0) {
            builder.append('[');
            Iterator<Component> parts = args.iterator();
            while (parts.hasNext()) {
                TextHelper.appendTextTo(builder, parts.next());
                while (parts.hasNext()) {
                    builder.append(", ");
                    TextHelper.appendTextTo(builder, parts.next());
                }
            }
            Joiner.on((String)", ").appendTo(builder, args);
            builder.append(']');
        }
    }

    private TextHelper() {
    }
}

