/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.io.Resources
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.world.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.factory.BlockFactory;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.gson.VectorAdapter;
import com.sk89q.worldedit.util.io.ResourceLoader;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

public final class LegacyMapper {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static LegacyMapper INSTANCE;
    private final ResourceLoader resourceLoader;
    private final Map<String, BlockState> stringToBlockMap = new HashMap<String, BlockState>();
    private final Multimap<BlockState, String> blockToStringMap = HashMultimap.create();
    private final Map<String, ItemType> stringToItemMap = new HashMap<String, ItemType>();
    private final Multimap<ItemType, String> itemToStringMap = HashMultimap.create();

    private LegacyMapper() {
        this.resourceLoader = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.CONFIGURATION).getResourceLoader();
        try {
            this.loadFromResource();
        }
        catch (Throwable e) {
            LOGGER.warn("Failed to load the built-in legacy id registry", e);
        }
    }

    private void loadFromResource() throws IOException {
        String value;
        String id;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Vector3.class, (Object)new VectorAdapter());
        Gson gson = gsonBuilder.disableHtmlEscaping().create();
        URL url = this.resourceLoader.getResource(LegacyMapper.class, "legacy.json");
        if (url == null) {
            throw new IOException("Could not find legacy.json");
        }
        String data = Resources.toString((URL)url, (Charset)Charset.defaultCharset());
        LegacyDataFile dataFile = (LegacyDataFile)gson.fromJson(data, new TypeToken<LegacyDataFile>(this){}.getType());
        DataFixer fixer = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).getDataFixer();
        ParserContext parserContext = new ParserContext();
        parserContext.setPreferringWildcard(false);
        parserContext.setRestricted(false);
        parserContext.setTryLegacy(false);
        for (Map.Entry<String, String> blockEntry : dataFile.blocks.entrySet()) {
            id = blockEntry.getKey();
            value = blockEntry.getValue();
            BlockState state = null;
            BlockFactory blockFactory = WorldEdit.getInstance().getBlockFactory();
            if (fixer != null) {
                try {
                    String newEntry = fixer.fixUp(DataFixer.FixTypes.BLOCK_STATE, value, 1631);
                    state = ((BaseBlock)blockFactory.parseFromInput(newEntry, parserContext)).toImmutableState();
                }
                catch (InputParseException inputParseException) {
                    // empty catch block
                }
            }
            if (state == null) {
                try {
                    state = ((BaseBlock)blockFactory.parseFromInput(value, parserContext)).toImmutableState();
                }
                catch (InputParseException inputParseException) {
                    // empty catch block
                }
            }
            if (state == null) {
                LOGGER.debug("Unknown block: " + value);
                continue;
            }
            this.blockToStringMap.put(state, (Object)id);
            this.stringToBlockMap.put(id, state);
        }
        for (Map.Entry<String, String> itemEntry : dataFile.items.entrySet()) {
            id = itemEntry.getKey();
            value = itemEntry.getValue();
            ItemType type = ItemTypes.get(value);
            if (type == null && fixer != null) {
                value = fixer.fixUp(DataFixer.FixTypes.ITEM_TYPE, value, 1631);
                type = ItemTypes.get(value);
            }
            if (type == null) {
                LOGGER.debug("Unknown item: " + value);
                continue;
            }
            this.itemToStringMap.put((Object)type, (Object)id);
            this.stringToItemMap.put(id, type);
        }
    }

    @Nullable
    public ItemType getItemFromLegacy(int legacyId) {
        return this.getItemFromLegacy(legacyId, 0);
    }

    @Nullable
    public ItemType getItemFromLegacy(int legacyId, int data) {
        return this.stringToItemMap.get(legacyId + ":" + data);
    }

    @Nullable
    public int[] getLegacyFromItem(ItemType itemType) {
        if (this.itemToStringMap.containsKey((Object)itemType)) {
            String value = (String)this.itemToStringMap.get((Object)itemType).stream().findFirst().get();
            return Arrays.stream(value.split(":")).mapToInt(Integer::parseInt).toArray();
        }
        return null;
    }

    @Nullable
    public BlockState getBlockFromLegacy(int legacyId) {
        return this.getBlockFromLegacy(legacyId, 0);
    }

    @Nullable
    public BlockState getBlockFromLegacy(int legacyId, int data) {
        return this.stringToBlockMap.get(legacyId + ":" + data);
    }

    @Nullable
    public int[] getLegacyFromBlock(BlockState blockState) {
        if (this.blockToStringMap.containsKey((Object)blockState)) {
            String value = (String)this.blockToStringMap.get((Object)blockState).stream().findFirst().get();
            return Arrays.stream(value.split(":")).mapToInt(Integer::parseInt).toArray();
        }
        return null;
    }

    public static LegacyMapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LegacyMapper();
        }
        return INSTANCE;
    }

    private static class LegacyDataFile {
        private Map<String, String> blocks;
        private Map<String, String> items;

        private LegacyDataFile() {
        }
    }
}

