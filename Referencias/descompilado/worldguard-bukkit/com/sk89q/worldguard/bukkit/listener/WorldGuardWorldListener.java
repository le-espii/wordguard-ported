/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.world.ChunkLoadEvent
 *  org.bukkit.event.world.WorldLoadEvent
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.BukkitConfigurationManager;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.util.Entities;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldGuardWorldListener
extends AbstractListener {
    private static final Logger log = Logger.getLogger(WorldGuardWorldListener.class.getCanonicalName());

    public WorldGuardWorldListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        BukkitConfigurationManager cfg = WorldGuardWorldListener.getConfig();
        if (cfg.activityHaltToggle) {
            int removed = 0;
            for (Entity entity : event.getChunk().getEntities()) {
                if (!Entities.isIntensiveEntity(BukkitAdapter.adapt((Entity)entity))) continue;
                entity.remove();
                ++removed;
            }
            if (removed > 50) {
                log.info("Halt-Act: " + removed + " entities (>50) auto-removed from " + event.getChunk().toString());
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.initWorld(event.getWorld());
    }

    public void initWorld(World world) {
        BukkitWorldConfiguration wcfg = WorldGuardWorldListener.getWorldConfig(world);
        if (wcfg.alwaysRaining && !wcfg.disableWeather) {
            world.setStorm(true);
        } else if (wcfg.disableWeather && !wcfg.alwaysRaining) {
            world.setStorm(false);
        }
        if (wcfg.alwaysThundering && !wcfg.disableThunder) {
            world.setThundering(true);
        } else if (wcfg.disableThunder && !wcfg.alwaysThundering) {
            world.setStorm(false);
        }
    }
}

