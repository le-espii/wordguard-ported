/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.factory;

import com.sk89q.worldedit.function.Contextual;
import com.sk89q.worldedit.function.EditContext;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.function.pattern.BiomePattern;

public class BiomeFactory
implements Contextual<RegionFunction> {
    private final BiomePattern biomeType;

    public BiomeFactory(BiomePattern biomeType) {
        this.biomeType = biomeType;
    }

    @Override
    public RegionFunction createFromContext(EditContext context) {
        return new BiomeReplace(context.getDestination(), this.biomeType);
    }

    public String toString() {
        return "set biome";
    }
}

