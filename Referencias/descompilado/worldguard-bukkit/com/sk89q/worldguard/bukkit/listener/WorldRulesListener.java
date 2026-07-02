/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.entity.EntityPotionEffectEvent
 *  org.bukkit.event.entity.EntityPotionEffectEvent$Cause
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class WorldRulesListener
extends AbstractListener {
    public WorldRulesListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onSpawnEntity(SpawnEntityEvent event) {
        if (event.getEffectiveType() == EntityType.EXPERIENCE_ORB) {
            BukkitWorldConfiguration config = WorldRulesListener.getWorldConfig(event.getWorld());
            if (config.disableExpDrops) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getCause() == EntityPotionEffectEvent.Cause.CONDUIT) {
            BukkitWorldConfiguration config = WorldRulesListener.getWorldConfig(event.getEntity().getWorld());
            if (config.disableConduitEffects) {
                event.setCancelled(true);
            }
        }
    }
}

