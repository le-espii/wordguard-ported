/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Hanging
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Painting
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.hanging.HangingBreakByEntityEvent
 *  org.bukkit.event.hanging.HangingBreakEvent
 *  org.bukkit.event.hanging.HangingBreakEvent$RemoveCause
 *  org.bukkit.projectiles.ProjectileSource
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
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.projectiles.ProjectileSource;

public class WorldGuardHangingListener
extends AbstractListener {
    public WorldGuardHangingListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onHangingBreak(HangingBreakEvent event) {
        Hanging hanging = event.getEntity();
        World world = hanging.getWorld();
        BukkitWorldConfiguration wcfg = WorldGuardHangingListener.getWorldConfig(world);
        if (event instanceof HangingBreakByEntityEvent) {
            HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent)event;
            Entity removerEntity = entityEvent.getRemover();
            if (removerEntity instanceof Projectile) {
                Projectile projectile = (Projectile)removerEntity;
                ProjectileSource remover = projectile.getShooter();
                LivingEntity livingEntity = removerEntity = remover instanceof LivingEntity ? (LivingEntity)remover : null;
            }
            if (!(removerEntity instanceof Player)) {
                if (removerEntity instanceof Creeper) {
                    if (wcfg.blockCreeperBlockDamage || wcfg.blockCreeperExplosions) {
                        event.setCancelled(true);
                        return;
                    }
                    if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)hanging.getLocation()), (RegionAssociable)null, Flags.CREEPER_EXPLOSION))) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (hanging instanceof Painting && (wcfg.blockEntityPaintingDestroy || wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)hanging.getLocation()), (RegionAssociable)null, Flags.ENTITY_PAINTING_DESTROY)))) {
                    event.setCancelled(true);
                } else if (hanging instanceof ItemFrame && (wcfg.blockEntityItemFrameDestroy || wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)hanging.getLocation()), (RegionAssociable)null, Flags.ENTITY_ITEM_FRAME_DESTROY)))) {
                    event.setCancelled(true);
                }
            }
        } else if (hanging instanceof Painting && wcfg.blockEntityPaintingDestroy && event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
            event.setCancelled(true);
        } else if (hanging instanceof ItemFrame && wcfg.blockEntityItemFrameDestroy && event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
            event.setCancelled(true);
        }
    }
}

