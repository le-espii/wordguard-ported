/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.lang.reflect.Type;
import org.checkerframework.checker.nullness.qual.Nullable;

class TextColorWrapper {
    final @Nullable TextColor color;
    final @Nullable TextDecoration decoration;
    final boolean reset;

    TextColorWrapper(@Nullable TextColor color, @Nullable TextDecoration decoration, boolean reset) {
        this.color = color;
        this.decoration = decoration;
        this.reset = reset;
    }

    private static @Nullable TextColor deserializeColor(JsonElement json, JsonDeserializationContext context) {
        try {
            return (TextColor)context.deserialize(json, TextColor.class);
        }
        catch (JsonParseException e) {
            return null;
        }
    }

    private static @Nullable TextDecoration deserializeDecoration(JsonElement json, JsonDeserializationContext context) {
        try {
            return (TextDecoration)context.deserialize(json, TextDecoration.class);
        }
        catch (JsonParseException e) {
            return null;
        }
    }

    static class Serializer
    implements JsonDeserializer<TextColorWrapper> {
        Serializer() {
        }

        public TextColorWrapper deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            boolean reset;
            TextColor color = TextColorWrapper.deserializeColor(json, context);
            TextDecoration decoration = color == null ? TextColorWrapper.deserializeDecoration(json, context) : null;
            boolean bl = reset = decoration == null && json.isJsonPrimitive() && json.getAsString().equals("reset");
            if (color == null && decoration == null && !reset) {
                throw new JsonParseException("Don't know how to parse " + json);
            }
            return new TextColorWrapper(color, decoration, reset);
        }
    }
}

