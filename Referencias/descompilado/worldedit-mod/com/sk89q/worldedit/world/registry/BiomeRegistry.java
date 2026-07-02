/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.biome.BiomeData;
import com.sk89q.worldedit.world.biome.BiomeType;
import javax.annotation.Nullable;

public interface BiomeRegistry {
    public Component getRichName(BiomeType var1);

    @Deprecated
    @Nullable
    public BiomeData getData(BiomeType var1);
}

