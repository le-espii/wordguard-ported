/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.DestroyEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.UseEntityEvent;
import com.sk89q.worldguard.bukkit.event.inventory.UseItemEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class BuildPermissionListener
extends AbstractListener {
    public BuildPermissionListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    private boolean hasBuildPermission(CommandSender sender, String perm) {
        return BuildPermissionListener.getPlugin().hasPermission(sender, "worldguard.build." + perm);
    }

    private void tellErrorMessage(CommandSender sender, World world) {
        String message = BuildPermissionListener.getWorldConfig((World)world).buildPermissionDenyMessage;
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlaceBlock(PlaceBlockEvent event) {
        Material material;
        Player player;
        if (!BuildPermissionListener.getWorldConfig((World)event.getWorld()).buildPermissions) {
            return;
        }
        Object rootCause = event.getCause().getRootCause();
        if (rootCause instanceof Player && !this.hasBuildPermission((CommandSender)(player = (Player)rootCause), "block." + (material = event.getEffectiveMaterial()).name().toLowerCase() + ".place") && !this.hasBuildPermission((CommandSender)player, "block.place." + material.name().toLowerCase())) {
            this.tellErrorMessage((CommandSender)player, event.getWorld());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBreakBlock(BreakBlockEvent event) {
        Material material;
        Player player;
        if (!BuildPermissionListener.getWorldConfig((World)event.getWorld()).buildPermissions) {
            return;
        }
        Object rootCause = event.getCause().getRootCause();
        if (rootCause instanceof Player && !this.hasBuildPermission((CommandSender)(player = (Player)rootCause), "block." + (material = event.getEffectiveMaterial()).name().toLowerCase() + ".remove") && !this.hasBuildPermission((CommandSender)player, "block.remove." + material.name().toLowerCase())) {
            this.tellErrorMessage((CommandSender)player, event.getWorld());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onUseBlock(UseBlockEvent event) {
        Player player;
        boolean blocked;
        if (!BuildPermissionListener.getWorldConfig((World)event.getWorld()).buildPermissions) {
            return;
        }
        Object rootCause = event.getCause().getRootCause();
        if (rootCause instanceof Player && (blocked = event.filter(arg_0 -> this.lambda$onUseBlock$0(player = (Player)rootCause, arg_0), true)) && !event.isSilent()) {
            this.tellErrorMessage((CommandSender)player, event.getWorld());
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onSpawnEntity(SpawnEntityEvent event) {
        EntityType type;
        Player player;
        if (!BuildPermissionListener.getWorldConfig((World)event.getWorld()).buildPermissions) {
            return;
        }
        Object rootCause = event.getCause().getRootCause();
        if (rootCause instanceof Player && !this.hasBuildPermission((CommandSender)(player = (Player)rootCause), "entity." + (type = event.getEffectiveType()).name().toLowerCase() + ".place") && !this.hasBuildPermission((CommandSender)player, "entity.place." + type.name().toLowerCase())) {
            this.tellErrorMessage((CommandSender)player, event.getWorld());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onDestroyEntity(DestroyEntityEvent event) {
        EntityType type;
        Player player;
        if (!BuildPermissionListener.getWorldConfig((World)event.getWorld()).buildPermissions) {
            return;
        }
        Object rootCause = event.getCause().getRootCause();
        if (rootCause instanceof Player && !this.hasBuildPermission((CommandSender)(player = (Player)rootCause), "entity." + (type = event.getEntity().getType()).name().toLowerCase() + ".remove") && !this.hasBuildPermission((CommandSender)player, "entity.remove." + type.name().toLowerCase())) {
            this.tellErrorMessage((CommandSender)player, event.getWorld());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onUseEntity(UseEntityEvent event) {
        EntityType type;
        Player player;
        if (!BuildPermissionListener.getWorldConfig((World)event.getWorld()).buildPermissions) {
            return;
        }
        Object rootCause = event.getCause().getRootCause();
        if (rootCause instanceof Player && !this.hasBuildPermission((CommandSender)(player = (Player)rootCause), "entity." + (type = event.getEntity().getType()).name().toLowerCase() + ".interact") && !this.hasBuildPermission((CommandSender)player, "entity.interact." + type.name().toLowerCase())) {
            this.tellErrorMessage((CommandSender)player, event.getWorld());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onDamageEntity(DamageEntityEvent event) {
        EntityType type;
        Player player;
        if (!BuildPermissionListener.getWorldConfig((World)event.getWorld()).buildPermissions) {
            return;
        }
        Object rootCause = event.getCause().getRootCause();
        if (rootCause instanceof Player && !this.hasBuildPermission((CommandSender)(player = (Player)rootCause), "entity." + (type = event.getEntity().getType()).name().toLowerCase() + ".damage") && !this.hasBuildPermission((CommandSender)player, "entity.damage." + type.name().toLowerCase())) {
            this.tellErrorMessage((CommandSender)player, event.getWorld());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onUseItem(UseItemEvent event) {
        if (!BuildPermissionListener.getWorldConfig((World)event.getWorld()).buildPermissions) {
            return;
        }
        Object rootCause = event.getCause().getRootCause();
        if (rootCause instanceof Player) {
            Player player = (Player)rootCause;
            Material material = event.getItemStack().getType();
            if (material.isBlock()) {
                return;
            }
            if (!this.hasBuildPermission((CommandSender)player, "item." + material.name().toLowerCase() + ".use") && !this.hasBuildPermission((CommandSender)player, "item.use." + material.name().toLowerCase())) {
                this.tellErrorMessage((CommandSender)player, event.getWorld());
                event.setCancelled(true);
            }
        }
    }

    private /* synthetic */ boolean lambda$onUseBlock$0(Player player, Location loc) {
        String blacklistName = loc.getBlock().getType().name().toLowerCase();
        return this.hasBuildPermission((CommandSender)player, "block." + blacklistName + ".interact") || this.hasBuildPermission((CommandSender)player, "block.interact." + blacklistName);
    }
}

