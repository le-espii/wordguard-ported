/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.weather;

import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.Registry;

public record WeatherType(String id) implements Keyed
{
    public static final Registry<WeatherType> REGISTRY = new Registry("weather type");

    public String getName() {
        return this.id();
    }

    public String toString() {
        return this.id();
    }
}

