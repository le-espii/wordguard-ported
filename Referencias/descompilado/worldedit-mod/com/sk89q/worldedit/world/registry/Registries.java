/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.world.registry.BiomeRegistry;
import com.sk89q.worldedit.world.registry.BlockCategoryRegistry;
import com.sk89q.worldedit.world.registry.BlockRegistry;
import com.sk89q.worldedit.world.registry.EntityRegistry;
import com.sk89q.worldedit.world.registry.ItemCategoryRegistry;
import com.sk89q.worldedit.world.registry.ItemRegistry;

public interface Registries {
    public BlockRegistry getBlockRegistry();

    public ItemRegistry getItemRegistry();

    public EntityRegistry getEntityRegistry();

    public BiomeRegistry getBiomeRegistry();

    public BlockCategoryRegistry getBlockCategoryRegistry();

    public ItemCategoryRegistry getItemCategoryRegistry();
}

