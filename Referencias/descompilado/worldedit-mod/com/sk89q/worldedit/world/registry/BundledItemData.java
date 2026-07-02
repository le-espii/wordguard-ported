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
import com.sk89q.worldedit.world.registry.BundledRegistries;
import com.sk89q.worldedit.world.registry.ItemMaterial;
import com.sk89q.worldedit.world.registry.SimpleItemMaterial;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

public final class BundledItemData {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static BundledItemData INSTANCE;
    private final Map<String, ItemEntry> idMap = new HashMap<String, ItemEntry>();

    private BundledItemData() {
        try {
            this.loadFromResource();
        }
        catch (Throwable e) {
            LOGGER.warn("Failed to load the built-in item registry", e);
        }
    }

    private void loadFromResource() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Vector3.class, (Object)new VectorAdapter());
        Gson gson = gsonBuilder.create();
        URL url = BundledRegistries.loadRegistry("items");
        LOGGER.debug("Using {} for bundled item data.", (Object)url);
        String data = Resources.toString((URL)url, (Charset)Charset.defaultCharset());
        List entries = (List)gson.fromJson(data, new TypeToken<List<ItemEntry>>(this){}.getType());
        for (ItemEntry entry : entries) {
            this.idMap.put(entry.id, entry);
        }
    }

    @Nullable
    public ItemEntry findById(String id) {
        if (!((String)id).contains(":")) {
            id = "minecraft:" + (String)id;
        }
        return this.idMap.get(id);
    }

    @Nullable
    public ItemMaterial getMaterialById(String id) {
        ItemEntry entry = this.findById(id);
        if (entry != null) {
            return new SimpleItemMaterial(entry.maxStackSize, entry.maxDamage);
        }
        return null;
    }

    public static BundledItemData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BundledItemData();
        }
        return INSTANCE;
    }

    public static class ItemEntry {
        private String id;
        private String unlocalizedName;
        public String localizedName;
        private int maxDamage;
        private int maxStackSize;
    }
}

