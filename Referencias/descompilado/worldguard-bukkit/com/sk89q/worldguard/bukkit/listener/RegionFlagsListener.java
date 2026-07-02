/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.bukkit.util.Materials;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

public class RegionFlagsListener
extends AbstractListener {
    public RegionFlagsListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onPlaceBlock(PlaceBlockEvent event) {
        if (!RegionFlagsListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Block block = event.getCause().getFirstBlock();
        if (block != null && Materials.isPistonBlock(block.getType())) {
            event.filter((Predicate)this.testState(query, Flags.PISTONS), false);
        }
        if (event.getCause().find(EntityType.SNOW_GOLEM) != null) {
            event.filter((Predicate)this.testState(query, Flags.SNOWMAN_TRAILS), false);
        }
        if (event.getCause().find(EntityType.ENDERMAN) != null) {
            event.filter((Predicate)this.testState(query, Flags.ENDER_BUILD), false);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onBreakBlock(BreakBlockEvent event) {
        if (!RegionFlagsListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        BukkitWorldConfiguration config = RegionFlagsListener.getWorldConfig(event.getWorld());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Block block = event.getCause().getFirstBlock();
        if (block != null && Materials.isPistonBlock(block.getType())) {
            event.filter((Predicate)this.testState(query, Flags.PISTONS), false);
        }
        if (event.getCause().find(EntityType.CREEPER) != null) {
            event.filter((Predicate)this.testState(query, Flags.CREEPER_EXPLOSION), config.explosionFlagCancellation);
        }
        if (event.getCause().find(EntityType.ENDER_DRAGON) != null) {
            event.filter((Predicate)this.testState(query, Flags.ENDERDRAGON_BLOCK_DAMAGE), config.explosionFlagCancellation);
        }
        if (event.getCause().find(EntityType.END_CRYSTAL) != null) {
            event.filter((Predicate)this.testState(query, Flags.OTHER_EXPLOSION), config.explosionFlagCancellation);
        }
        if (event.getCause().find(EntityType.ENDERMAN) != null) {
            event.filter((Predicate)this.testState(query, Flags.ENDER_BUILD), false);
        }
        if (event.getCause().find(EntityType.RAVAGER) != null) {
            event.filter((Predicate)this.testState(query, Flags.RAVAGER_RAVAGE), false);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        World world = entity.getWorld();
        if (!RegionFlagsListener.isRegionSupportEnabled(world)) {
            return;
        }
        if (Entities.isNPC(entity)) {
            return;
        }
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            if (!query.testState(BukkitAdapter.adapt((Location)entity.getLocation()), localPlayer, Flags.FALL_DAMAGE)) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            if (!query.testState(BukkitAdapter.adapt((Location)entity.getLocation()), localPlayer, Flags.FALL_DAMAGE)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private Predicate<Location> testState(RegionQuery query, StateFlag flag) {
        return location -> query.testState(BukkitAdapter.adapt((Location)location), (RegionAssociable)null, flag);
    }
}

