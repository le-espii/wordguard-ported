/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 */
package com.sk89q.worldedit.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.gson.BlockVectorAdapter;
import com.sk89q.worldedit.util.gson.VectorAdapter;

public final class GsonUtil {
    private static final Gson gson = new Gson();

    private GsonUtil() {
    }

    public static GsonBuilder createBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Vector3.class, (Object)new VectorAdapter());
        gsonBuilder.registerTypeAdapter(BlockVector3.class, (Object)new BlockVectorAdapter());
        return gsonBuilder;
    }

    public static String stringValue(String s) {
        return gson.toJson((Object)s);
    }
}

