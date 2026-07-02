/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package com.sk89q.worldedit.util.formatting.text.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sk89q.worldedit.util.formatting.text.util.NameMap;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class NameMapSerializer<E extends Enum<E>>
implements JsonDeserializer<E>,
JsonSerializer<E> {
    private final String name;
    private final NameMap<E> map;

    public NameMapSerializer(String name, NameMap<E> map) {
        this.name = name;
        this.map = map;
    }

    public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        String string = json.getAsString();
        Optional<E> value = this.map.value(string);
        if (value.isPresent()) {
            return (E)((Enum)value.get());
        }
        throw new JsonParseException("invalid " + this.name + ":  " + string);
    }

    public JsonElement serialize(E src, Type typeOfT, JsonSerializationContext context) {
        Enum hack = (Enum)Objects.requireNonNull(src);
        return new JsonPrimitive(this.map.name(hack));
    }
}

