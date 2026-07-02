/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.worldguard.bukkit.listener;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitConfigurationManager;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

class AbstractListener
implements Listener {
    private final WorldGuardPlugin plugin;

    public AbstractListener(WorldGuardPlugin plugin) {
        Preconditions.checkNotNull((Object)((Object)plugin));
        this.plugin = plugin;
    }

    public void registerEvents() {
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }

    protected static WorldGuardPlugin getPlugin() {
        return WorldGuardPlugin.inst();
    }

    protected static BukkitConfigurationManager getConfig() {
        return AbstractListener.getPlugin().getConfigManager();
    }

    protected static BukkitWorldConfiguration getWorldConfig(String world) {
        return AbstractListener.getConfig().get(world);
    }

    protected static BukkitWorldConfiguration getWorldConfig(World world) {
        return AbstractListener.getWorldConfig(world.getName());
    }

    protected static BukkitWorldConfiguration getWorldConfig(LocalPlayer player) {
        return AbstractListener.getWorldConfig(((BukkitPlayer)player).getPlayer().getWorld());
    }

    protected static boolean isRegionSupportEnabled(World world) {
        return AbstractListener.getWorldConfig((World)world).useRegions;
    }

    protected RegionAssociable createRegionAssociable(Cause cause) {
        Player player;
        Object rootCause = cause.getRootCause();
        if (!cause.isKnown()) {
            return Associables.constant(Association.NON_MEMBER);
        }
        if (rootCause instanceof Player && !Entities.isNPC((Entity)(player = (Player)rootCause))) {
            return AbstractListener.getPlugin().wrapPlayer(player);
        }
        if (rootCause instanceof OfflinePlayer) {
            OfflinePlayer offlinePlayer = (OfflinePlayer)rootCause;
            return AbstractListener.getPlugin().wrapOfflinePlayer(offlinePlayer);
        }
        if (rootCause instanceof Entity) {
            Location loc;
            Entity entity = (Entity)rootCause;
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            BukkitWorldConfiguration config = AbstractListener.getWorldConfig(entity.getWorld());
            if (PaperLib.isPaper() && config.usePaperEntityOrigin) {
                loc = entity.getOrigin();
                if (loc == null || loc.getWorld() == null) {
                    loc = entity.getLocation();
                }
            } else {
                loc = entity.getLocation();
            }
            return new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt((Location)loc), config.useMaxPriorityAssociation);
        }
        if (rootCause instanceof Block) {
            Block block = (Block)rootCause;
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            Location loc = block.getLocation();
            return new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt((Location)loc), AbstractListener.getWorldConfig((World)loc.getWorld()).useMaxPriorityAssociation);
        }
        return Associables.constant(Association.NON_MEMBER);
    }
}

