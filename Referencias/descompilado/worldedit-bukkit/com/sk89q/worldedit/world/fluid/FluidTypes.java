/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.fluid;

import com.sk89q.worldedit.world.fluid.FluidType;
import javax.annotation.Nullable;

public final class FluidTypes {
    public static final FluidType EMPTY = FluidTypes.register("minecraft:empty");
    public static final FluidType FLOWING_LAVA = FluidTypes.register("minecraft:flowing_lava");
    public static final FluidType FLOWING_WATER = FluidTypes.register("minecraft:flowing_water");
    public static final FluidType LAVA = FluidTypes.register("minecraft:lava");
    public static final FluidType WATER = FluidTypes.register("minecraft:water");

    private FluidTypes() {
    }

    private static FluidType register(String id) {
        return FluidTypes.register(new FluidType(id));
    }

    public static FluidType register(FluidType fluid) {
        return FluidType.REGISTRY.register(fluid.id(), fluid);
    }

    @Nullable
    public static FluidType get(String id) {
        return FluidType.REGISTRY.get(id);
    }
}

