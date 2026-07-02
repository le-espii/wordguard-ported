/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 */
package com.sk89q.worldedit.extent.clipboard.io.legacycompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.NBTCompatibilityHandler;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BaseBlock;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTagType;

public class SignCompatibilityHandler
implements NBTCompatibilityHandler {
    @Override
    public BaseBlock updateNbt(BaseBlock block) {
        if (!DeprecationUtil.isSign(block.getBlockType())) {
            return block;
        }
        LinCompoundTag tag = block.getNbt();
        if (tag == null) {
            return block;
        }
        LinCompoundTag.Builder newTag = tag.toBuilder();
        for (int i = 0; i < 4; ++i) {
            String key = "Text" + (i + 1);
            LinStringTag value = tag.findTag(key, LinTagType.stringTag());
            if (value == null) continue;
            String storedString = value.value();
            JsonElement jsonElement = null;
            if (storedString.startsWith("{")) {
                try {
                    jsonElement = JsonParser.parseString((String)storedString);
                }
                catch (JsonSyntaxException jsonSyntaxException) {
                    // empty catch block
                }
            }
            if (jsonElement == null) {
                jsonElement = new JsonPrimitive(storedString);
            }
            if (jsonElement.isJsonObject()) continue;
            if (jsonElement.isJsonNull()) {
                jsonElement = new JsonPrimitive("");
            }
            JsonObject jsonTextObject = new JsonObject();
            jsonTextObject.add("text", jsonElement);
            newTag.put("Text" + (i + 1), LinStringTag.of(jsonTextObject.toString()));
        }
        block = block.toBaseBlock(LazyReference.computed(newTag.build()));
        return block;
    }
}

