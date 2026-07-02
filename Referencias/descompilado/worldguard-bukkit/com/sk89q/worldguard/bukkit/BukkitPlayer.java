/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.bukkit.BukkitPlayer
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.World
 *  com.sk89q.worldedit.world.weather.WeatherType
 *  com.sk89q.worldedit.world.weather.WeatherTypes
 *  org.bukkit.BanList$Type
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.WeatherType
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldguard.bukkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldedit.world.weather.WeatherTypes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.util.MessagingUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BukkitPlayer
extends com.sk89q.worldedit.bukkit.BukkitPlayer
implements LocalPlayer {
    protected final WorldGuardPlugin plugin;
    private final boolean silenced;
    private String name;

    public BukkitPlayer(WorldGuardPlugin plugin, Player player) {
        this(plugin, player, false);
    }

    BukkitPlayer(WorldGuardPlugin plugin, Player player, boolean silenced) {
        super(player);
        this.plugin = plugin;
        this.silenced = silenced;
    }

    public String getName() {
        if (this.name == null) {
            this.name = this.getPlayer().getName();
        }
        return this.name;
    }

    @Override
    public boolean hasGroup(String group) {
        return this.plugin.inGroup((OfflinePlayer)this.getPlayer(), group);
    }

    @Override
    public void kick(String msg) {
        if (!this.silenced) {
            this.getPlayer().kickPlayer(msg);
        }
    }

    @Override
    public void ban(String msg) {
        if (!this.silenced) {
            Bukkit.getBanList((BanList.Type)BanList.Type.NAME).addBan(this.getName(), null, null, null);
            this.getPlayer().kickPlayer(msg);
        }
    }

    @Override
    public double getHealth() {
        return this.getPlayer().getHealth();
    }

    @Override
    public void setHealth(double health) {
        this.getPlayer().setHealth(health);
    }

    @Override
    public double getMaxHealth() {
        return this.getPlayer().getMaxHealth();
    }

    @Override
    public double getFoodLevel() {
        return this.getPlayer().getFoodLevel();
    }

    @Override
    public void setFoodLevel(double foodLevel) {
        this.getPlayer().setFoodLevel((int)foodLevel);
    }

    @Override
    public double getSaturation() {
        return this.getPlayer().getSaturation();
    }

    @Override
    public void setSaturation(double saturation) {
        this.getPlayer().setSaturation((float)saturation);
    }

    @Override
    public float getExhaustion() {
        return this.getPlayer().getExhaustion();
    }

    @Override
    public void setExhaustion(float exhaustion) {
        this.getPlayer().setExhaustion(exhaustion);
    }

    @Override
    public WeatherType getPlayerWeather() {
        org.bukkit.WeatherType playerWeather = this.getPlayer().getPlayerWeather();
        return playerWeather == null ? null : (playerWeather == org.bukkit.WeatherType.CLEAR ? WeatherTypes.CLEAR : WeatherTypes.RAIN);
    }

    @Override
    public void setPlayerWeather(WeatherType weather) {
        this.getPlayer().setPlayerWeather(weather == WeatherTypes.CLEAR ? org.bukkit.WeatherType.CLEAR : org.bukkit.WeatherType.DOWNFALL);
    }

    @Override
    public void resetPlayerWeather() {
        this.getPlayer().resetPlayerWeather();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return this.getPlayer().isPlayerTimeRelative();
    }

    @Override
    public long getPlayerTimeOffset() {
        return this.getPlayer().getPlayerTimeOffset();
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        this.getPlayer().setPlayerTime(time, relative);
    }

    @Override
    public void resetPlayerTime() {
        this.getPlayer().resetPlayerTime();
    }

    @Override
    public int getFireTicks() {
        return this.getPlayer().getFireTicks();
    }

    @Override
    public void setFireTicks(int fireTicks) {
        this.getPlayer().setFireTicks(fireTicks);
    }

    @Override
    public void setCompassTarget(Location location) {
        this.getPlayer().setCompassTarget(BukkitAdapter.adapt((Location)location));
    }

    @Override
    public void sendTitle(String title, String subtitle) {
        if (WorldGuard.getInstance().getPlatform().getGlobalStateManager().get((World)this.getWorld()).forceDefaultTitleTimes) {
            this.getPlayer().sendTitle(title, subtitle, 10, 70, 20);
        } else {
            this.getPlayer().sendTitle(title, subtitle, -1, -1, -1);
        }
    }

    @Override
    public void resetFallDistance() {
        this.getPlayer().setFallDistance(0.0f);
    }

    @Override
    public void teleport(Location location, String successMessage, String failMessage) {
        PaperLib.teleportAsync((Entity)this.getPlayer(), BukkitAdapter.adapt((Location)location)).thenApply(success -> {
            if (success.booleanValue()) {
                if (!successMessage.isEmpty()) {
                    MessagingUtil.sendStringToChat(this, successMessage);
                }
            } else {
                this.printError(failMessage);
            }
            return success;
        });
    }

    public String[] getGroups() {
        return this.plugin.getGroups((OfflinePlayer)this.getPlayer());
    }

    public void printRaw(String msg) {
        if (!this.silenced) {
            super.printRaw(msg);
        }
    }

    public boolean hasPermission(String perm) {
        return this.plugin.hasPermission((CommandSender)this.getPlayer(), perm);
    }
}

