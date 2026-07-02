/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package com.sk89q.worldedit.util.formatting.text.serializer.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.kyori.Key;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class ComponentSerializerImpl
implements JsonDeserializer<Component>,
JsonSerializer<Component> {
    static final ComponentSerializerImpl INSTANCE = new ComponentSerializerImpl();
    static final String TEXT = "text";
    static final String TRANSLATE = "translate";
    static final String TRANSLATE_WITH = "with";
    static final String SCORE = "score";
    static final String SCORE_NAME = "name";
    static final String SCORE_OBJECTIVE = "objective";
    static final String SCORE_VALUE = "value";
    static final String SELECTOR = "selector";
    static final String KEYBIND = "keybind";
    static final String EXTRA = "extra";
    static final String NBT = "nbt";
    static final String NBT_INTERPRET = "interpret";
    static final String NBT_BLOCK = "block";
    static final String NBT_ENTITY = "entity";
    static final String NBT_STORAGE = "storage";

    ComponentSerializerImpl() {
    }

    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return this.deserialize0(json, context);
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private BuildableComponent<?, ?> deserialize0(JsonElement element, JsonDeserializationContext context) throws JsonParseException {
        Style style;
        ComponentBuilder<TextComponent, TextComponent.Builder> component;
        if (element.isJsonPrimitive()) {
            return TextComponent.of(element.getAsString());
        }
        if (element.isJsonArray()) {
            void var3_4;
            Object var3_3 = null;
            for (JsonElement childElement : element.getAsJsonArray()) {
                BuildableComponent<?, ?> child = this.deserialize0(childElement, context);
                if (var3_4 == null) {
                    Object obj = child.toBuilder();
                    continue;
                }
                var3_4.append((Component)child);
            }
            if (var3_4 != null) return var3_4.build();
            throw ComponentSerializerImpl.notSureHowToDeserialize(element);
        }
        if (!element.isJsonObject()) {
            throw ComponentSerializerImpl.notSureHowToDeserialize(element);
        }
        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject.has(TEXT)) {
            component = TextComponent.builder(jsonObject.get(TEXT).getAsString());
        } else if (jsonObject.has(TRANSLATE)) {
            String key = jsonObject.get(TRANSLATE).getAsString();
            if (!jsonObject.has(TRANSLATE_WITH)) {
                component = TranslatableComponent.builder(key);
            } else {
                JsonArray with = jsonObject.getAsJsonArray(TRANSLATE_WITH);
                ArrayList args = new ArrayList(with.size());
                int size = with.size();
                for (int i = 0; i < size; ++i) {
                    JsonElement argElement = with.get(i);
                    args.add(this.deserialize0(argElement, context));
                }
                component = TranslatableComponent.builder(key).args(args);
            }
        } else if (jsonObject.has(SCORE)) {
            JsonObject score = jsonObject.getAsJsonObject(SCORE);
            if (!score.has(SCORE_NAME) || !score.has(SCORE_OBJECTIVE)) {
                throw new JsonParseException("A score component requires a name and objective");
            }
            ScoreComponent.Builder builder = ScoreComponent.builder().name(score.get(SCORE_NAME).getAsString()).objective(score.get(SCORE_OBJECTIVE).getAsString());
            component = score.has(SCORE_VALUE) ? builder.value(score.get(SCORE_VALUE).getAsString()) : builder;
        } else if (jsonObject.has(SELECTOR)) {
            component = SelectorComponent.builder().pattern(jsonObject.get(SELECTOR).getAsString());
        } else if (jsonObject.has(KEYBIND)) {
            component = KeybindComponent.builder().keybind(jsonObject.get(KEYBIND).getAsString());
        } else {
            boolean interpret;
            if (!jsonObject.has(NBT)) throw ComponentSerializerImpl.notSureHowToDeserialize(element);
            String nbt = jsonObject.get(NBT).getAsString();
            boolean bl = interpret = jsonObject.has(NBT_INTERPRET) && jsonObject.getAsJsonPrimitive(NBT_INTERPRET).getAsBoolean();
            if (jsonObject.has(NBT_BLOCK)) {
                BlockNbtComponent.Pos pos = (BlockNbtComponent.Pos)context.deserialize(jsonObject.get(NBT_BLOCK), BlockNbtComponent.Pos.class);
                component = ComponentSerializerImpl.nbt(BlockNbtComponent.builder(), nbt, interpret).pos(pos);
            } else if (jsonObject.has(NBT_ENTITY)) {
                component = ComponentSerializerImpl.nbt(EntityNbtComponent.builder(), nbt, interpret).selector(jsonObject.get(NBT_ENTITY).getAsString());
            } else {
                if (!jsonObject.has(NBT_STORAGE)) throw ComponentSerializerImpl.notSureHowToDeserialize(element);
                component = ComponentSerializerImpl.nbt(StorageNbtComponent.builder(), nbt, interpret).storage(Key.of(jsonObject.get(NBT_STORAGE).getAsString()));
            }
        }
        if (jsonObject.has(EXTRA)) {
            JsonArray extra = jsonObject.getAsJsonArray(EXTRA);
            int size = extra.size();
            for (int i = 0; i < size; ++i) {
                JsonElement extraElement = extra.get(i);
                component.append((Component)this.deserialize0(extraElement, context));
            }
        }
        if ((style = (Style)context.deserialize(element, Style.class)).isEmpty()) return component.build();
        component.style(style);
        return component.build();
    }

    private static <C extends NbtComponent<C, B>, B extends NbtComponentBuilder<C, B>> B nbt(B builder, String nbt, boolean interpret) {
        return builder.nbtPath(nbt).interpret(interpret);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement style;
        JsonObject object = new JsonObject();
        if (src instanceof TextComponent) {
            object.addProperty(TEXT, ((TextComponent)src).content());
        } else if (src instanceof TranslatableComponent) {
            TranslatableComponent tc = (TranslatableComponent)src;
            object.addProperty(TRANSLATE, tc.key());
            if (!tc.args().isEmpty()) {
                JsonArray with = new JsonArray();
                for (Component arg : tc.args()) {
                    with.add(context.serialize((Object)arg));
                }
                object.add(TRANSLATE_WITH, (JsonElement)with);
            }
        } else if (src instanceof ScoreComponent) {
            ScoreComponent sc = (ScoreComponent)src;
            JsonObject score = new JsonObject();
            score.addProperty(SCORE_NAME, sc.name());
            score.addProperty(SCORE_OBJECTIVE, sc.objective());
            String value = sc.value();
            if (value != null) {
                score.addProperty(SCORE_VALUE, value);
            }
            object.add(SCORE, (JsonElement)score);
        } else if (src instanceof SelectorComponent) {
            object.addProperty(SELECTOR, ((SelectorComponent)src).pattern());
        } else if (src instanceof KeybindComponent) {
            object.addProperty(KEYBIND, ((KeybindComponent)src).keybind());
        } else {
            if (!(src instanceof NbtComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(src);
            NbtComponent nc = (NbtComponent)src;
            object.addProperty(NBT, nc.nbtPath());
            object.addProperty(NBT_INTERPRET, Boolean.valueOf(nc.interpret()));
            if (src instanceof BlockNbtComponent) {
                JsonElement position = context.serialize((Object)((BlockNbtComponent)nc).pos());
                object.add(NBT_BLOCK, position);
            } else if (src instanceof EntityNbtComponent) {
                object.addProperty(NBT_ENTITY, ((EntityNbtComponent)nc).selector());
            } else {
                if (!(src instanceof StorageNbtComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(src);
                object.addProperty(NBT_STORAGE, ((StorageNbtComponent)nc).storage().asString());
            }
        }
        List<Component> children = src.children();
        if (!children.isEmpty()) {
            JsonArray extra = new JsonArray();
            for (Component child : children) {
                extra.add(context.serialize((Object)child));
            }
            object.add(EXTRA, (JsonElement)extra);
        }
        if (!src.hasStyling() || !(style = context.serialize((Object)src.style())).isJsonObject()) return object;
        for (Map.Entry entry : ((JsonObject)style).entrySet()) {
            object.add((String)entry.getKey(), (JsonElement)entry.getValue());
        }
        return object;
    }

    private static JsonParseException notSureHowToDeserialize(JsonElement element) {
        return new JsonParseException("Don't know how to turn " + element + " into a Component");
    }

    private static IllegalArgumentException notSureHowToSerialize(Component component) {
        return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
    }
}

