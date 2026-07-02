/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.weather;

import com.sk89q.worldedit.world.weather.WeatherType;
import javax.annotation.Nullable;

public final class WeatherTypes {
    public static final WeatherType CLEAR = WeatherTypes.register("clear");
    public static final WeatherType RAIN = WeatherTypes.register("rain");
    public static final WeatherType THUNDER_STORM = WeatherTypes.register("thunder_storm");

    private WeatherTypes() {
    }

    private static WeatherType register(String id) {
        return WeatherTypes.register(new WeatherType(id));
    }

    public static WeatherType register(WeatherType weather) {
        return WeatherType.REGISTRY.register(weather.id(), weather);
    }

    @Nullable
    public static WeatherType get(String id) {
        return WeatherType.REGISTRY.get(id);
    }
}

