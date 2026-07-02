/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.weather.WeatherType
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;

public class WeatherLockFlag
extends FlagValueChangeHandler<WeatherType> {
    public static final Factory FACTORY = new Factory();
    private WeatherType initialWeather;

    public WeatherLockFlag(Session session) {
        super(session, Flags.WEATHER_LOCK);
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, WeatherType value) {
        this.initialWeather = player.getPlayerWeather();
        if (value != null) {
            player.setPlayerWeather(value);
        }
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, WeatherType currentValue, WeatherType lastValue, MoveType moveType) {
        if (lastValue == null) {
            this.initialWeather = player.getPlayerWeather();
        }
        player.setPlayerWeather(currentValue);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, WeatherType lastValue, MoveType moveType) {
        if (this.initialWeather != null) {
            player.setPlayerWeather(this.initialWeather);
        } else {
            player.resetPlayerWeather();
        }
        this.initialWeather = null;
        return true;
    }

    public static class Factory
    extends Handler.Factory<WeatherLockFlag> {
        @Override
        public WeatherLockFlag create(Session session) {
            return new WeatherLockFlag(session);
        }
    }
}

