/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.generation;

import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;

public record ConfiguredFeatureType(String id) implements Keyed
{
    public static final NamespacedRegistry<ConfiguredFeatureType> REGISTRY = new NamespacedRegistry("configured feature type");

    public String toString() {
        return this.id;
    }
}

