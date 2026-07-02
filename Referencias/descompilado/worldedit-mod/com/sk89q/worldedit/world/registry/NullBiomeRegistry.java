/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.translation.TranslationManager;
import com.sk89q.worldedit.world.biome.BiomeData;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import javax.annotation.Nullable;

public class NullBiomeRegistry
implements BiomeRegistry {
    @Override
    public Component getRichName(BiomeType biomeType) {
        return TranslatableComponent.of(TranslationManager.makeTranslationKey("biome", biomeType.id()));
    }

    @Override
    @Deprecated
    @Nullable
    public BiomeData getData(BiomeType biome) {
        return null;
    }
}

