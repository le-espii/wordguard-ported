/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.fluid;

import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;

public record FluidType(String id) implements Keyed
{
    public static final NamespacedRegistry<FluidType> REGISTRY = new NamespacedRegistry("fluid type");

    public String toString() {
        return this.id();
    }
}

