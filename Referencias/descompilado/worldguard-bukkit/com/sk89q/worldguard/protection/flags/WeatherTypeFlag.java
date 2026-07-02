/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.weather.WeatherType
 *  com.sk89q.worldedit.world.weather.WeatherTypes
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldedit.world.weather.WeatherTypes;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import javax.annotation.Nullable;

@Deprecated
public class WeatherTypeFlag
extends Flag<WeatherType> {
    protected WeatherTypeFlag(String name, @Nullable RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    protected WeatherTypeFlag(String name) {
        super(name);
    }

    @Override
    public WeatherType parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        WeatherType weatherType = this.unmarshal(input = input.trim());
        if (weatherType == null) {
            throw new InvalidFlagFormat("Unknown weather type: " + input);
        }
        return weatherType;
    }

    @Override
    public WeatherType unmarshal(@Nullable Object o) {
        return WeatherTypes.get((String)String.valueOf(o).toLowerCase());
    }

    @Override
    public Object marshal(WeatherType o) {
        return o.id();
    }
}

