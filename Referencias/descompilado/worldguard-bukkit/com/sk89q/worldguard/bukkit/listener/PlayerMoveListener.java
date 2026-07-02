/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.util.Location
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.AbstractHorse
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityMountEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.vehicle.VehicleEnterEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.util.Vector
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class PlayerMoveListener
extends AbstractListener {
    public PlayerMoveListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerEvents() {
        if (WorldGuard.getInstance().getPlatform().getGlobalStateManager().usePlayerMove) {
            PluginManager pm = PlayerMoveListener.getPlugin().getServer().getPluginManager();
            pm.registerEvents((Listener)this, (Plugin)PlayerMoveListener.getPlugin());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        LocalPlayer player = PlayerMoveListener.getPlugin().wrapPlayer(event.getPlayer());
        Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(player);
        session.testMoveTo(player, BukkitAdapter.adapt((Location)event.getRespawnLocation()), MoveType.RESPAWN, true);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        if (entity instanceof Player) {
            LocalPlayer player = PlayerMoveListener.getPlugin().wrapPlayer((Player)entity);
            Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(player);
            if (null != session.testMoveTo(player, BukkitAdapter.adapt((Location)event.getVehicle().getLocation()), MoveType.EMBARK, true)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        LocalPlayer localPlayer = PlayerMoveListener.getPlugin().wrapPlayer(player);
        Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(localPlayer);
        MoveType moveType = MoveType.MOVE;
        if (event.getPlayer().isGliding()) {
            moveType = MoveType.GLIDE;
        } else if (event.getPlayer().isSwimming()) {
            moveType = MoveType.SWIM;
        } else if (event.getPlayer().getVehicle() != null && event.getPlayer().getVehicle() instanceof AbstractHorse) {
            moveType = MoveType.RIDE;
        }
        com.sk89q.worldedit.util.Location weLocation = session.testMoveTo(localPlayer, BukkitAdapter.adapt((Location)to), moveType);
        if (weLocation != null) {
            Location override = BukkitAdapter.adapt((com.sk89q.worldedit.util.Location)weLocation);
            override.setX((double)override.getBlockX() + 0.5);
            override.setY((double)override.getBlockY());
            override.setZ((double)override.getBlockZ() + 0.5);
            override.setPitch(to.getPitch());
            override.setYaw(to.getYaw());
            event.setTo(override.clone());
            Entity vehicle = player.getVehicle();
            if (vehicle != null) {
                vehicle.eject();
                for (Entity current = vehicle; current != null; current = current.getVehicle()) {
                    current.eject();
                    vehicle.setVelocity(new Vector());
                    if (vehicle instanceof LivingEntity) {
                        vehicle.teleport(override.clone());
                        continue;
                    }
                    vehicle.teleport(override.clone().add(0.0, 1.0, 0.0));
                }
                player.teleport(override.clone().add(0.0, 1.0, 0.0));
                Bukkit.getScheduler().runTaskLater((Plugin)PlayerMoveListener.getPlugin(), () -> player.teleport(override.clone().add(0.0, 1.0, 0.0)), 1L);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LocalPlayer localPlayer = PlayerMoveListener.getPlugin().wrapPlayer(player);
        Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(localPlayer);
        com.sk89q.worldedit.util.Location loc = session.testMoveTo(localPlayer, BukkitAdapter.adapt((Location)event.getPlayer().getLocation()), MoveType.OTHER_CANCELLABLE);
        if (loc != null) {
            player.teleport(BukkitAdapter.adapt((com.sk89q.worldedit.util.Location)loc));
        }
        session.uninitialize(localPlayer);
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            LocalPlayer player = PlayerMoveListener.getPlugin().wrapPlayer((Player)entity);
            Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(player);
            if (null != session.testMoveTo(player, BukkitAdapter.adapt((Location)event.getMount().getLocation()), MoveType.EMBARK, true)) {
                event.setCancelled(true);
            }
        }
    }
}

