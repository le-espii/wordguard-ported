/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.block.Biome
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.translation.TranslationManager;
import com.sk89q.worldedit.world.biome.BiomeData;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import javax.annotation.Nullable;
import org.bukkit.block.Biome;

class BukkitBiomeRegistry
implements BiomeRegistry {
    BukkitBiomeRegistry() {
    }

    @Override
    public Component getRichName(BiomeType biomeType) {
        return TranslatableComponent.of(TranslationManager.makeTranslationKey("biome", biomeType.id()));
    }

    @Override
    @Deprecated
    @Nullable
    public BiomeData getData(BiomeType biome) {
        Biome bukkitBiome = BukkitAdapter.adapt(biome);
        return bukkitBiome == null ? null : () -> bukkitBiome.name();
    }
}

