/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_156
 *  net.minecraft.class_2960
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.biome.BiomeData;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import net.minecraft.class_156;
import net.minecraft.class_2960;

class FabricBiomeRegistry
implements BiomeRegistry {
    FabricBiomeRegistry() {
    }

    @Override
    public Component getRichName(BiomeType biomeType) {
        return TranslatableComponent.of(class_156.method_646((String)"biome", (class_2960)class_2960.method_60654((String)biomeType.id())));
    }

    @Override
    @Deprecated
    public BiomeData getData(BiomeType biome) {
        return new FabricBiomeData(biome);
    }

    @Deprecated
    private static class FabricBiomeData
    implements BiomeData {
        private final BiomeType biome;

        private FabricBiomeData(BiomeType biome) {
            this.biome = biome;
        }

        @Override
        public String getName() {
            return this.biome.id();
        }
    }
}

