/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package com.sk89q.worldedit.util.formatting.text.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.TextColorWrapper;
import java.lang.reflect.Type;

public class StyleSerializer
implements JsonDeserializer<Style>,
JsonSerializer<Style> {
    public static final StyleSerializer INSTANCE = new StyleSerializer();
    private static final TextDecoration[] DECORATIONS = TextDecoration.values();
    static final String COLOR = "color";
    static final String INSERTION = "insertion";
    static final String CLICK_EVENT = "clickEvent";
    static final String CLICK_EVENT_ACTION = "action";
    static final String CLICK_EVENT_VALUE = "value";
    static final String HOVER_EVENT = "hoverEvent";
    static final String HOVER_EVENT_ACTION = "action";
    static final String HOVER_EVENT_VALUE = "value";

    public Style deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        return this.deserialize(object, context);
    }

    private Style deserialize(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        JsonObject hoverEvent;
        String value;
        JsonPrimitive rawValue;
        ClickEvent.Action action;
        JsonObject clickEvent;
        Style.Builder style = Style.builder();
        if (json.has(COLOR)) {
            TextColorWrapper color = (TextColorWrapper)context.deserialize(json.get(COLOR), TextColorWrapper.class);
            if (color.color != null) {
                style.color(color.color);
            } else if (color.decoration != null) {
                style.decoration(color.decoration, true);
            }
        }
        for (TextDecoration decoration : DECORATIONS) {
            String name = TextDecoration.NAMES.name(decoration);
            if (!json.has(name)) continue;
            style.decoration(decoration, json.get(name).getAsBoolean());
        }
        if (json.has(INSERTION)) {
            style.insertion(json.get(INSERTION).getAsString());
        }
        if (json.has(CLICK_EVENT) && (clickEvent = json.getAsJsonObject(CLICK_EVENT)) != null) {
            JsonPrimitive rawAction = clickEvent.getAsJsonPrimitive("action");
            ClickEvent.Action action2 = action = rawAction == null ? null : (ClickEvent.Action)((Object)context.deserialize((JsonElement)rawAction, ClickEvent.Action.class));
            if (action != null && action.readable()) {
                rawValue = clickEvent.getAsJsonPrimitive("value");
                String string = value = rawValue == null ? null : rawValue.getAsString();
                if (value != null) {
                    style.clickEvent(ClickEvent.of(action, value));
                }
            }
        }
        if (json.has(HOVER_EVENT) && (hoverEvent = json.getAsJsonObject(HOVER_EVENT)) != null) {
            JsonPrimitive rawAction = hoverEvent.getAsJsonPrimitive("action");
            Enum enum_ = action = rawAction == null ? null : (HoverEvent.Action)((Object)context.deserialize((JsonElement)rawAction, HoverEvent.Action.class));
            if (action != null && ((HoverEvent.Action)((Object)action)).readable()) {
                rawValue = hoverEvent.get("value");
                String string = value = rawValue == null ? null : (Component)context.deserialize((JsonElement)rawValue, Component.class);
                if (value != null) {
                    style.hoverEvent(HoverEvent.of((HoverEvent.Action)((Object)action), (Component)((Object)value)));
                }
            }
        }
        return style.build();
    }

    public JsonElement serialize(Style src, Type typeOfSrc, JsonSerializationContext context) {
        HoverEvent hoverEvent;
        ClickEvent clickEvent;
        JsonObject json = new JsonObject();
        TextColor color = src.color();
        if (color != null) {
            json.add(COLOR, context.serialize((Object)color));
        }
        for (TextDecoration decoration : DECORATIONS) {
            TextDecoration.State state = src.decoration(decoration);
            if (state == TextDecoration.State.NOT_SET) continue;
            String name = TextDecoration.NAMES.name(decoration);
            json.addProperty(name, Boolean.valueOf(state == TextDecoration.State.TRUE));
        }
        String insertion = src.insertion();
        if (insertion != null) {
            json.add(INSERTION, context.serialize((Object)insertion));
        }
        if ((clickEvent = src.clickEvent()) != null) {
            JsonObject eventJson = new JsonObject();
            eventJson.add("action", context.serialize((Object)clickEvent.action()));
            eventJson.addProperty("value", clickEvent.value());
            json.add(CLICK_EVENT, (JsonElement)eventJson);
        }
        if ((hoverEvent = src.hoverEvent()) != null) {
            JsonObject eventJson = new JsonObject();
            eventJson.add("action", context.serialize((Object)hoverEvent.action()));
            eventJson.add("value", context.serialize((Object)hoverEvent.value()));
            json.add(HOVER_EVENT, (JsonElement)eventJson);
        }
        return json;
    }
}

