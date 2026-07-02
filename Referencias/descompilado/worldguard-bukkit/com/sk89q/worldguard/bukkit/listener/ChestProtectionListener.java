/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.ChatColor
 *  org.bukkit.Keyed
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Tag
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.block.SignChangeEvent
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.DelegateEvent;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

public class ChestProtectionListener
extends AbstractListener {
    public ChestProtectionListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    private void sendMessage(DelegateEvent event, Player player, String message) {
        if (!event.isSilent()) {
            player.sendMessage(message);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlaceBlock(PlaceBlockEvent event) {
        Player player = event.getCause().getFirstPlayer();
        if (player != null) {
            BukkitWorldConfiguration wcfg = ChestProtectionListener.getWorldConfig(event.getWorld());
            if (!wcfg.signChestProtection) {
                return;
            }
            event.filter(target -> {
                if (wcfg.getChestProtection().isChest(BukkitAdapter.asBlockType((Material)event.getEffectiveMaterial())) && wcfg.isChestProtected(BukkitAdapter.adapt((Location)target.getBlock().getLocation()), WorldGuardPlugin.inst().wrapPlayer(player))) {
                    this.sendMessage(event, player, String.valueOf(ChatColor.DARK_RED) + "This spot is for a chest that you don't have permission for.");
                    return false;
                }
                return true;
            }, true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBreakBlock(BreakBlockEvent event) {
        Player player = event.getCause().getFirstPlayer();
        BukkitWorldConfiguration wcfg = ChestProtectionListener.getWorldConfig(event.getWorld());
        if (!wcfg.signChestProtection) {
            return;
        }
        if (player != null) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            event.filter(target -> {
                if (wcfg.isChestProtected(BukkitAdapter.adapt((Location)target.getBlock().getLocation()), localPlayer)) {
                    this.sendMessage(event, player, String.valueOf(ChatColor.DARK_RED) + "This chest is protected.");
                    return false;
                }
                return true;
            }, true);
        } else {
            event.filter(target -> !wcfg.isChestProtected(BukkitAdapter.adapt((Location)target.getBlock().getLocation())));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onUseBlock(UseBlockEvent event) {
        Player player = event.getCause().getFirstPlayer();
        BukkitWorldConfiguration wcfg = ChestProtectionListener.getWorldConfig(event.getWorld());
        if (!wcfg.signChestProtection) {
            return;
        }
        if (player != null) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            event.filter(target -> {
                if (wcfg.isChestProtected(BukkitAdapter.adapt((Location)target.getBlock().getLocation()), localPlayer)) {
                    this.sendMessage(event, player, String.valueOf(ChatColor.DARK_RED) + "This chest is protected.");
                    return false;
                }
                return true;
            }, true);
        } else {
            event.filter(target -> !wcfg.isChestProtected(BukkitAdapter.adapt((Location)target.getBlock().getLocation())));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        BukkitWorldConfiguration wcfg = ChestProtectionListener.getWorldConfig(event.getBlock().getWorld());
        if (wcfg.signChestProtection) {
            if ("[Lock]".equalsIgnoreCase(event.getLine(0))) {
                if (wcfg.isChestProtectedPlacement(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), WorldGuardPlugin.inst().wrapPlayer(player))) {
                    player.sendMessage(String.valueOf(ChatColor.DARK_RED) + "You do not own the adjacent chest.");
                    event.getBlock().breakNaturally();
                    event.setCancelled(true);
                    return;
                }
                if (!Tag.STANDING_SIGNS.isTagged((Keyed)event.getBlock().getType())) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "The [Lock] sign must be a sign post, not a wall sign.");
                    event.getBlock().breakNaturally();
                    event.setCancelled(true);
                    return;
                }
                if (!player.getName().equalsIgnoreCase(event.getLine(1))) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "The first owner line must be your name.");
                    event.getBlock().breakNaturally();
                    event.setCancelled(true);
                    return;
                }
                Material below = event.getBlock().getRelative(0, -1, 0).getType();
                if (below == Material.TNT || below == Material.SAND || below == Material.GRAVEL || Tag.STANDING_SIGNS.isTagged((Keyed)below)) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "That is not a safe block that you're putting this sign on.");
                    event.getBlock().breakNaturally();
                    event.setCancelled(true);
                    return;
                }
                event.setLine(0, "[Lock]");
                player.sendMessage(String.valueOf(ChatColor.YELLOW) + "A chest or double chest above is now protected.");
            }
        } else if (!wcfg.disableSignChestProtectionCheck && "[Lock]".equalsIgnoreCase(event.getLine(0))) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "WorldGuard's sign chest protection is disabled.");
            event.getBlock().breakNaturally();
            event.setCancelled(true);
        }
    }
}

