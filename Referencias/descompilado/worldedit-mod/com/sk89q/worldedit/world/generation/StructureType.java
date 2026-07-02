/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.generation;

import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;

public record StructureType(String id) implements Keyed
{
    public static final NamespacedRegistry<StructureType> REGISTRY = new NamespacedRegistry("structure type");

    public String toString() {
        return this.id;
    }
}

