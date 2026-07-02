/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 */
package com.sk89q.worldedit.util.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sk89q.worldedit.math.BlockVector3;
import java.lang.reflect.Type;

public class BlockVectorAdapter
implements JsonDeserializer<BlockVector3> {
    public BlockVector3 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = json.getAsJsonArray();
        if (jsonArray.size() != 3) {
            throw new JsonParseException("Expected array of 3 length for BlockVector3");
        }
        double x = jsonArray.get(0).getAsInt();
        double y = jsonArray.get(1).getAsInt();
        double z = jsonArray.get(2).getAsInt();
        return BlockVector3.at(x, y, z);
    }
}

