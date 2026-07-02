/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableRangeMap
 *  com.google.common.collect.Range
 *  com.google.common.collect.RangeMap
 *  com.google.common.collect.TreeRangeMap
 */
package com.sk89q.worldedit.world.registry;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.util.io.ResourceLoader;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import com.sk89q.worldedit.world.registry.BlockCategoryRegistry;
import com.sk89q.worldedit.world.registry.BlockRegistry;
import com.sk89q.worldedit.world.registry.BundledBlockRegistry;
import com.sk89q.worldedit.world.registry.BundledItemRegistry;
import com.sk89q.worldedit.world.registry.EntityRegistry;
import com.sk89q.worldedit.world.registry.ItemCategoryRegistry;
import com.sk89q.worldedit.world.registry.ItemRegistry;
import com.sk89q.worldedit.world.registry.NullBiomeRegistry;
import com.sk89q.worldedit.world.registry.NullBlockCategoryRegistry;
import com.sk89q.worldedit.world.registry.NullEntityRegistry;
import com.sk89q.worldedit.world.registry.NullItemCategoryRegistry;
import com.sk89q.worldedit.world.registry.Registries;
import java.io.IOException;
import java.net.URL;

public class BundledRegistries
implements Registries {
    private static final BundledRegistries INSTANCE = new BundledRegistries();
    private static final RangeMap<Integer, String> VERSION_MAP;
    private final BundledBlockRegistry blockRegistry = new BundledBlockRegistry();
    private final BundledItemRegistry itemRegistry = new BundledItemRegistry();
    private final NullEntityRegistry entityRegistry = new NullEntityRegistry();
    private final NullBiomeRegistry biomeRegistry = new NullBiomeRegistry();
    private final NullBlockCategoryRegistry blockCategoryRegistry = new NullBlockCategoryRegistry();
    private final NullItemCategoryRegistry itemCategoryRegistry = new NullItemCategoryRegistry();

    static URL loadRegistry(String name) throws IOException {
        int dataVersion;
        String version;
        ResourceLoader resourceLoader = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.CONFIGURATION).getResourceLoader();
        URL url = resourceLoader.getResource(BundledRegistries.class, name + "." + (version = (String)VERSION_MAP.get((Comparable)Integer.valueOf(dataVersion = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).getDataVersion()))) + ".json");
        if (url == null) {
            url = resourceLoader.getResource(BundledRegistries.class, name + ".json");
        }
        if (url == null) {
            throw new IOException("Could not find " + name + ".json");
        }
        return url;
    }

    protected BundledRegistries() {
    }

    @Override
    public BlockRegistry getBlockRegistry() {
        return this.blockRegistry;
    }

    @Override
    public ItemRegistry getItemRegistry() {
        return this.itemRegistry;
    }

    @Override
    public EntityRegistry getEntityRegistry() {
        return this.entityRegistry;
    }

    @Override
    public BiomeRegistry getBiomeRegistry() {
        return this.biomeRegistry;
    }

    @Override
    public BlockCategoryRegistry getBlockCategoryRegistry() {
        return this.blockCategoryRegistry;
    }

    @Override
    public ItemCategoryRegistry getItemCategoryRegistry() {
        return this.itemCategoryRegistry;
    }

    public static BundledRegistries getInstance() {
        return INSTANCE;
    }

    static {
        TreeRangeMap versionMap = TreeRangeMap.create();
        versionMap.put(Range.atLeast((Comparable)Integer.valueOf(3463)), (Object)"120");
        versionMap.put(Range.atLeast((Comparable)Integer.valueOf(3953)), (Object)"121");
        versionMap.put(Range.atLeast((Comparable)Integer.valueOf(4082)), (Object)"1213");
        VERSION_MAP = ImmutableRangeMap.copyOf((RangeMap)versionMap);
    }
}

