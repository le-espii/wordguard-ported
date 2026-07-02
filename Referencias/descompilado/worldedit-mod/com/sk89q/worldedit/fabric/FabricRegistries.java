/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.worldedit.fabric.FabricBiomeRegistry;
import com.sk89q.worldedit.fabric.FabricBlockCategoryRegistry;
import com.sk89q.worldedit.fabric.FabricBlockRegistry;
import com.sk89q.worldedit.fabric.FabricItemCategoryRegistry;
import com.sk89q.worldedit.fabric.FabricItemRegistry;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import com.sk89q.worldedit.world.registry.BlockCategoryRegistry;
import com.sk89q.worldedit.world.registry.BlockRegistry;
import com.sk89q.worldedit.world.registry.BundledRegistries;
import com.sk89q.worldedit.world.registry.ItemCategoryRegistry;
import com.sk89q.worldedit.world.registry.ItemRegistry;

class FabricRegistries
extends BundledRegistries {
    private static final FabricRegistries INSTANCE = new FabricRegistries();
    private final BlockRegistry blockRegistry = new FabricBlockRegistry();
    private final BiomeRegistry biomeRegistry = new FabricBiomeRegistry();
    private final ItemRegistry itemRegistry = new FabricItemRegistry();
    private final BlockCategoryRegistry blockCategoryRegistry = new FabricBlockCategoryRegistry();
    private final ItemCategoryRegistry itemCategoryRegistry = new FabricItemCategoryRegistry();

    FabricRegistries() {
    }

    @Override
    public BlockRegistry getBlockRegistry() {
        return this.blockRegistry;
    }

    @Override
    public BiomeRegistry getBiomeRegistry() {
        return this.biomeRegistry;
    }

    @Override
    public ItemRegistry getItemRegistry() {
        return this.itemRegistry;
    }

    @Override
    public BlockCategoryRegistry getBlockCategoryRegistry() {
        return this.blockCategoryRegistry;
    }

    @Override
    public ItemCategoryRegistry getItemCategoryRegistry() {
        return this.itemCategoryRegistry;
    }

    public static FabricRegistries getInstance() {
        return INSTANCE;
    }
}

