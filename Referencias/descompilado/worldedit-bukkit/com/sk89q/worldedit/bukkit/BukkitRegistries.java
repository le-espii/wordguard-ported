/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.bukkit.BukkitBiomeRegistry;
import com.sk89q.worldedit.bukkit.BukkitBlockCategoryRegistry;
import com.sk89q.worldedit.bukkit.BukkitBlockRegistry;
import com.sk89q.worldedit.bukkit.BukkitItemCategoryRegistry;
import com.sk89q.worldedit.bukkit.BukkitItemRegistry;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import com.sk89q.worldedit.world.registry.BlockCategoryRegistry;
import com.sk89q.worldedit.world.registry.BlockRegistry;
import com.sk89q.worldedit.world.registry.BundledRegistries;
import com.sk89q.worldedit.world.registry.ItemCategoryRegistry;
import com.sk89q.worldedit.world.registry.ItemRegistry;

class BukkitRegistries
extends BundledRegistries {
    private static final BukkitRegistries INSTANCE = new BukkitRegistries();
    private final BlockRegistry blockRegistry = new BukkitBlockRegistry();
    private final BiomeRegistry biomeRegistry = new BukkitBiomeRegistry();
    private final ItemRegistry itemRegistry = new BukkitItemRegistry();
    private final BlockCategoryRegistry blockCategoryRegistry = new BukkitBlockCategoryRegistry();
    private final ItemCategoryRegistry itemCategoryRegistry = new BukkitItemCategoryRegistry();

    BukkitRegistries() {
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

    public static BukkitRegistries getInstance() {
        return INSTANCE;
    }
}

