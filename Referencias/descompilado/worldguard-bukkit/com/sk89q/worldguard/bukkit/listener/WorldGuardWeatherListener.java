/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.weather.LightningStrikeEvent
 *  org.bukkit.event.weather.ThunderChangeEvent
 *  org.bukkit.event.weather.WeatherChangeEvent
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldGuardWeatherListener
extends AbstractListener {
    public WorldGuardWeatherListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onWeatherChange(WeatherChangeEvent event) {
        BukkitWorldConfiguration wcfg = WorldGuardWeatherListener.getWorldConfig(event.getWorld());
        if (event.toWeatherState()) {
            if (wcfg.disableWeather) {
                event.setCancelled(true);
            }
        } else if (!wcfg.disableWeather && wcfg.alwaysRaining) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onThunderChange(ThunderChangeEvent event) {
        BukkitWorldConfiguration wcfg = WorldGuardWeatherListener.getWorldConfig(event.getWorld());
        if (event.toThunderState()) {
            if (wcfg.disableThunder) {
                event.setCancelled(true);
            }
        } else if (!wcfg.disableWeather && wcfg.alwaysThundering) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onLightningStrike(LightningStrikeEvent event) {
        BukkitWorldConfiguration wcfg = WorldGuardWeatherListener.getWorldConfig(event.getWorld());
        if (!wcfg.disallowedLightningBlocks.isEmpty()) {
            Block target = event.getLightning().getLocation().getBlock();
            Material targetId = target.getType();
            if (targetId == Material.AIR) {
                targetId = target.getRelative(BlockFace.DOWN).getType();
            }
            if (wcfg.disallowedLightningBlocks.contains(BukkitAdapter.asBlockType((Material)targetId).getId())) {
                event.setCancelled(true);
            }
        }
        Location loc = event.getLightning().getLocation();
        if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)loc), (RegionAssociable)null, Flags.LIGHTNING))) {
            event.setCancelled(true);
        }
    }
}

