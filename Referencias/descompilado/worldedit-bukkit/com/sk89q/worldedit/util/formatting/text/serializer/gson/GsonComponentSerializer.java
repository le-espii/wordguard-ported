/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sk89q.worldedit.util.formatting.text.BlockNbtComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.serializer.ComponentSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.BlockNbtComponentPosSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.ComponentSerializerImpl;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.NameMapSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.StyleSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.TextColorWrapper;
import java.lang.reflect.Type;
import org.checkerframework.checker.nullness.qual.NonNull;

public class GsonComponentSerializer
implements ComponentSerializer<Component, Component, String>,
JsonDeserializer<Component>,
JsonSerializer<Component> {
    public static final GsonComponentSerializer INSTANCE = new GsonComponentSerializer();
    static final Gson GSON = GsonComponentSerializer.populate(new GsonBuilder()).create();

    public static @NonNull GsonBuilder populate(@NonNull GsonBuilder builder) {
        builder.registerTypeHierarchyAdapter(Component.class, (Object)ComponentSerializerImpl.INSTANCE).registerTypeAdapter(Style.class, (Object)StyleSerializer.INSTANCE).registerTypeAdapter(ClickEvent.Action.class, new NameMapSerializer<ClickEvent.Action>("click action", ClickEvent.Action.NAMES)).registerTypeAdapter(HoverEvent.Action.class, new NameMapSerializer<HoverEvent.Action>("hover action", HoverEvent.Action.NAMES)).registerTypeAdapter(TextColorWrapper.class, (Object)new TextColorWrapper.Serializer()).registerTypeAdapter(TextColor.class, new NameMapSerializer<TextColor>("text color", TextColor.NAMES)).registerTypeAdapter(TextDecoration.class, new NameMapSerializer<TextDecoration>("text decoration", TextDecoration.NAMES)).registerTypeHierarchyAdapter(BlockNbtComponent.Pos.class, (Object)BlockNbtComponentPosSerializer.INSTANCE);
        return builder;
    }

    @Override
    public @NonNull Component deserialize(@NonNull String string) {
        return (Component)GSON.fromJson(string, Component.class);
    }

    @Override
    public @NonNull String serialize(@NonNull Component component) {
        return GSON.toJson((Object)component);
    }

    @Deprecated
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ComponentSerializerImpl.INSTANCE.deserialize(json, typeOfT, context);
    }

    @Deprecated
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        return ComponentSerializerImpl.INSTANCE.serialize(src, typeOfSrc, context);
    }
}

