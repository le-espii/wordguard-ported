/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.gamemode;

import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.Registry;

public record GameMode(String id) implements Keyed
{
    public static final Registry<GameMode> REGISTRY = new Registry("game mode");

    public String getName() {
        return this.id();
    }

    public String toString() {
        return this.id();
    }
}

