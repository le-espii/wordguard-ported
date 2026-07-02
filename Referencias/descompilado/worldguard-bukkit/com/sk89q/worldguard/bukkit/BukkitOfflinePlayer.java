/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.blocks.BaseItemStack
 *  com.sk89q.worldedit.entity.BaseEntity
 *  com.sk89q.worldedit.extent.inventory.BlockBag
 *  com.sk89q.worldedit.math.Vector3
 *  com.sk89q.worldedit.session.SessionKey
 *  com.sk89q.worldedit.util.HandSide
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.World
 *  com.sk89q.worldedit.world.weather.WeatherType
 *  javax.annotation.Nullable
 *  org.bukkit.OfflinePlayer
 */
package com.sk89q.worldguard.bukkit;

import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;

class BukkitOfflinePlayer
extends BukkitPlayer {
    private final OfflinePlayer player;

    BukkitOfflinePlayer(WorldGuardPlugin plugin, OfflinePlayer offlinePlayer) {
        super(plugin, offlinePlayer.getPlayer());
        this.player = offlinePlayer;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public boolean hasGroup(String group) {
        return this.plugin.inGroup(this.player, group);
    }

    @Override
    public String[] getGroups() {
        return this.plugin.getGroups(this.player);
    }

    @Override
    public boolean hasPermission(String perm) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void kick(String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void ban(String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getHealth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHealth(double health) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getMaxHealth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getFoodLevel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFoodLevel(double foodLevel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getSaturation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSaturation(double saturation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getExhaustion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExhaustion(float exhaustion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WeatherType getPlayerWeather() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPlayerWeather(WeatherType weather) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetPlayerWeather() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getPlayerTimeOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetPlayerTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void printRaw(String msg) {
        throw new UnsupportedOperationException();
    }

    public void printDebug(String msg) {
        throw new UnsupportedOperationException();
    }

    public void print(String msg) {
        throw new UnsupportedOperationException();
    }

    public void printError(String msg) {
        throw new UnsupportedOperationException();
    }

    public World getWorld() {
        throw new UnsupportedOperationException();
    }

    public BaseItemStack getItemInHand(HandSide handSide) {
        throw new UnsupportedOperationException();
    }

    public void giveItem(BaseItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    public BlockBag getInventoryBlockBag() {
        throw new UnsupportedOperationException();
    }

    public void setPosition(Vector3 pos, float pitch, float yaw) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public BaseEntity getState() {
        throw new UnsupportedOperationException();
    }

    public Location getLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCompassTarget(Location location) {
        throw new UnsupportedOperationException();
    }

    public SessionKey getSessionKey() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public <T> T getFacet(Class<? extends T> cls) {
        throw new UnsupportedOperationException();
    }
}

