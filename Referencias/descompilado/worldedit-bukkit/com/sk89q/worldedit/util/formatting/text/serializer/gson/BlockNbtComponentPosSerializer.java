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
import com.sk89q.worldedit.util.formatting.text.BlockNbtComponent;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockNbtComponentPosSerializer
implements JsonDeserializer<BlockNbtComponent.Pos>,
JsonSerializer<BlockNbtComponent.Pos> {
    public static final BlockNbtComponentPosSerializer INSTANCE = new BlockNbtComponentPosSerializer();
    private static final Pattern LOCAL_PATTERN = Pattern.compile("^\\^(\\d+(\\.\\d+)?) \\^(\\d+(\\.\\d+)?) \\^(\\d+(\\.\\d+)?)$");
    private static final Pattern WORLD_PATTERN = Pattern.compile("^(~?)(\\d+) (~?)(\\d+) (~?)(\\d+)$");
    private static final String LOCAL_SYMBOL = "^";
    private static final String RELATIVE_SYMBOL = "~";
    private static final String ABSOLUTE_SYMBOL = "";

    public BlockNbtComponent.Pos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String string = json.getAsString();
        Matcher localMatch = LOCAL_PATTERN.matcher(string);
        if (localMatch.matches()) {
            return BlockNbtComponent.LocalPos.of(Double.parseDouble(localMatch.group(1)), Double.parseDouble(localMatch.group(3)), Double.parseDouble(localMatch.group(5)));
        }
        Matcher worldMatch = WORLD_PATTERN.matcher(string);
        if (worldMatch.matches()) {
            return BlockNbtComponent.WorldPos.of(BlockNbtComponentPosSerializer.deserializeCoordinate(worldMatch.group(1), worldMatch.group(2)), BlockNbtComponentPosSerializer.deserializeCoordinate(worldMatch.group(3), worldMatch.group(4)), BlockNbtComponentPosSerializer.deserializeCoordinate(worldMatch.group(5), worldMatch.group(6)));
        }
        throw new JsonParseException("Don't know how to turn " + string + " into a Position");
    }

    public JsonElement serialize(BlockNbtComponent.Pos src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof BlockNbtComponent.LocalPos) {
            BlockNbtComponent.LocalPos local = (BlockNbtComponent.LocalPos)src;
            return new JsonPrimitive(BlockNbtComponentPosSerializer.serializeLocal(local.left()) + ' ' + BlockNbtComponentPosSerializer.serializeLocal(local.up()) + ' ' + BlockNbtComponentPosSerializer.serializeLocal(local.forwards()));
        }
        if (src instanceof BlockNbtComponent.WorldPos) {
            BlockNbtComponent.WorldPos world = (BlockNbtComponent.WorldPos)src;
            return new JsonPrimitive(BlockNbtComponentPosSerializer.serializeCoordinate(world.x()) + ' ' + BlockNbtComponentPosSerializer.serializeCoordinate(world.y()) + ' ' + BlockNbtComponentPosSerializer.serializeCoordinate(world.z()));
        }
        throw new IllegalArgumentException("Don't know how to serialize " + src + " as a Position");
    }

    private static BlockNbtComponent.WorldPos.Coordinate deserializeCoordinate(String prefix, String value) {
        int i = Integer.parseInt(value);
        if (prefix.isEmpty()) {
            return BlockNbtComponent.WorldPos.Coordinate.absolute(i);
        }
        if (prefix.equals(RELATIVE_SYMBOL)) {
            return BlockNbtComponent.WorldPos.Coordinate.relative(i);
        }
        throw new AssertionError();
    }

    private static String serializeLocal(double value) {
        return LOCAL_SYMBOL + value;
    }

    private static String serializeCoordinate(BlockNbtComponent.WorldPos.Coordinate coordinate) {
        return (coordinate.type() == BlockNbtComponent.WorldPos.Coordinate.Type.RELATIVE ? RELATIVE_SYMBOL : ABSOLUTE_SYMBOL) + coordinate.value();
    }
}

