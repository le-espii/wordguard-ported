/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.sk89q.worldedit.entity.Player
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.weather.WeatherType
 */
package com.sk89q.worldguard;

import com.google.common.annotations.Beta;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;

public interface LocalPlayer
extends Player,
RegionAssociable {
    public boolean hasGroup(String var1);

    public void kick(String var1);

    public void ban(String var1);

    @Override
    default public Association getAssociation(List<ProtectedRegion> regions) {
        boolean member = false;
        for (ProtectedRegion region : regions) {
            if (region.isOwner(this)) {
                return Association.OWNER;
            }
            if (member || !region.isMember(this)) continue;
            member = true;
        }
        return member ? Association.MEMBER : Association.NON_MEMBER;
    }

    public double getHealth();

    public void setHealth(double var1);

    public double getMaxHealth();

    public double getFoodLevel();

    public void setFoodLevel(double var1);

    public double getSaturation();

    public void setSaturation(double var1);

    public float getExhaustion();

    public void setExhaustion(float var1);

    public WeatherType getPlayerWeather();

    public void setPlayerWeather(WeatherType var1);

    public void resetPlayerWeather();

    public boolean isPlayerTimeRelative();

    public long getPlayerTimeOffset();

    public void setPlayerTime(long var1, boolean var3);

    public void resetPlayerTime();

    public int getFireTicks();

    public void setFireTicks(int var1);

    public void setCompassTarget(Location var1);

    @Beta
    public void sendTitle(String var1, String var2);

    public void resetFallDistance();

    public void teleport(Location var1, String var2, String var3);
}

