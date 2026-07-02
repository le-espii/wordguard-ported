/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.Resources
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.world.registry;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.gson.VectorAdapter;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.BundledRegistries;
import com.sk89q.worldedit.world.registry.SimpleBlockMaterial;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

public final class BundledBlockData {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static BundledBlockData INSTANCE;
    private final Map<String, BlockEntry> idMap = new HashMap<String, BlockEntry>();

    private BundledBlockData() {
        try {
            this.loadFromResource();
        }
        catch (Throwable e) {
            LOGGER.warn("Failed to load the built-in block registry", e);
        }
    }

    private void loadFromResource() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Vector3.class, (Object)new VectorAdapter());
        Gson gson = gsonBuilder.create();
        URL url = BundledRegistries.loadRegistry("blocks");
        LOGGER.debug("Using {} for bundled block data.", (Object)url);
        String data = Resources.toString((URL)url, (Charset)Charset.defaultCharset());
        List entries = (List)gson.fromJson(data, new TypeToken<List<BlockEntry>>(this){}.getType());
        for (BlockEntry entry : entries) {
            this.idMap.put(entry.id, entry);
        }
    }

    @Nullable
    public BlockEntry findById(String id) {
        if (!((String)id).contains(":")) {
            id = "minecraft:" + (String)id;
        }
        return this.idMap.get(id);
    }

    @Nullable
    public BlockMaterial getMaterialById(String id) {
        BlockEntry entry = this.findById(id);
        if (entry != null) {
            return entry.material;
        }
        return null;
    }

    public static BundledBlockData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BundledBlockData();
        }
        return INSTANCE;
    }

    public static class BlockEntry {
        private String id;
        public String localizedName;
        private final SimpleBlockMaterial material = new SimpleBlockMaterial();
    }
}

