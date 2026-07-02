/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.fluid;

import com.sk89q.worldedit.world.fluid.FluidCategory;
import javax.annotation.Nullable;

public final class FluidCategories {
    public static final FluidCategory LAVA = FluidCategories.register("minecraft:lava");
    public static final FluidCategory WATER = FluidCategories.register("minecraft:water");

    private FluidCategories() {
    }

    private static FluidCategory register(String id) {
        return FluidCategories.register(new FluidCategory(id));
    }

    public static FluidCategory register(FluidCategory tag) {
        return FluidCategory.REGISTRY.register(tag.id(), tag);
    }

    @Nullable
    public static FluidCategory get(String id) {
        return FluidCategory.REGISTRY.get(id);
    }
}

