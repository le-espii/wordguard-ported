/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.resources.ResourceLocation
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.biome.BiomeData;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

class NeoForgeBiomeRegistry
implements BiomeRegistry {
    NeoForgeBiomeRegistry() {
    }

    @Override
    public Component getRichName(BiomeType biomeType) {
        return TranslatableComponent.of(Util.makeDescriptionId((String)"biome", (ResourceLocation)ResourceLocation.parse((String)biomeType.id())));
    }

    @Override
    @Deprecated
    public BiomeData getData(BiomeType biome) {
        return new ForgeBiomeData(biome);
    }

    @Deprecated
    private static class ForgeBiomeData
    implements BiomeData {
        private final BiomeType biome;

        private ForgeBiomeData(BiomeType biome) {
            this.biome = biome;
        }

        @Override
        public String getName() {
            return this.biome.id();
        }
    }
}

