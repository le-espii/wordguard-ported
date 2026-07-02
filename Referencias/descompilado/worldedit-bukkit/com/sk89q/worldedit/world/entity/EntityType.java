/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.entity;

import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;

public record EntityType(String id) implements Keyed
{
    public static final NamespacedRegistry<EntityType> REGISTRY = new NamespacedRegistry("entity type", true);

    public EntityType {
        if (!((String)id).contains(":")) {
            id = "minecraft:" + (String)id;
        }
    }

    public String getName() {
        return this.id();
    }

    public String toString() {
        return this.id();
    }
}

