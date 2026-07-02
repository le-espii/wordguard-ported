/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.util.Location
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.vehicle.VehicleMoveEvent
 *  org.bukkit.util.Vector
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.util.Locations;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class WorldGuardVehicleListener
extends AbstractListener {
    public WorldGuardVehicleListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (vehicle.getPassengers().isEmpty()) {
            return;
        }
        List playerPassengers = vehicle.getPassengers().stream().filter(ent -> ent instanceof Player).map(ent -> (Player)ent).collect(Collectors.toList());
        if (playerPassengers.isEmpty()) {
            return;
        }
        World world = vehicle.getWorld();
        BukkitWorldConfiguration wcfg = WorldGuardVehicleListener.getWorldConfig(world);
        if (wcfg.useRegions && Locations.isDifferentBlock(BukkitAdapter.adapt((Location)event.getFrom()), BukkitAdapter.adapt((Location)event.getTo()))) {
            for (Player player : playerPassengers) {
                if (Entities.isNPC((Entity)player)) continue;
                LocalPlayer localPlayer = WorldGuardVehicleListener.getPlugin().wrapPlayer(player);
                com.sk89q.worldedit.util.Location lastValid = WorldGuard.getInstance().getPlatform().getSessionManager().get(localPlayer).testMoveTo(localPlayer, BukkitAdapter.adapt((Location)event.getTo()), MoveType.RIDE);
                if (lastValid == null) continue;
                vehicle.setVelocity(new Vector(0, 0, 0));
                vehicle.teleport(event.getFrom());
                if (Locations.isDifferentBlock(lastValid, BukkitAdapter.adapt((Location)event.getFrom()))) {
                    Vector dir = player.getLocation().getDirection();
                    player.teleport(BukkitAdapter.adapt((com.sk89q.worldedit.util.Location)lastValid).setDirection(dir));
                }
                return;
            }
        }
    }
}

