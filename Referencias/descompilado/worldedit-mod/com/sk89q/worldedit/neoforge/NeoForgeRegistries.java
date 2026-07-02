/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.neoforge.NeoForgeBiomeRegistry;
import com.sk89q.worldedit.neoforge.NeoForgeBlockCategoryRegistry;
import com.sk89q.worldedit.neoforge.NeoForgeBlockRegistry;
import com.sk89q.worldedit.neoforge.NeoForgeItemCategoryRegistry;
import com.sk89q.worldedit.neoforge.NeoForgeItemRegistry;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import com.sk89q.worldedit.world.registry.BlockCategoryRegistry;
import com.sk89q.worldedit.world.registry.BlockRegistry;
import com.sk89q.worldedit.world.registry.BundledRegistries;
import com.sk89q.worldedit.world.registry.ItemCategoryRegistry;
import com.sk89q.worldedit.world.registry.ItemRegistry;

class NeoForgeRegistries
extends BundledRegistries {
    private static final NeoForgeRegistries INSTANCE = new NeoForgeRegistries();
    private final BlockRegistry blockRegistry = new NeoForgeBlockRegistry();
    private final BiomeRegistry biomeRegistry = new NeoForgeBiomeRegistry();
    private final ItemRegistry itemRegistry = new NeoForgeItemRegistry();
    private final BlockCategoryRegistry blockCategoryRegistry = new NeoForgeBlockCategoryRegistry();
    private final ItemCategoryRegistry itemCategoryRegistry = new NeoForgeItemCategoryRegistry();

    NeoForgeRegistries() {
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

    public static NeoForgeRegistries getInstance() {
        return INSTANCE;
    }
}

