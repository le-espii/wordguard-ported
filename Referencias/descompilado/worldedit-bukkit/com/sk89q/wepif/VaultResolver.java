/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.RegisteredServiceProvider
 */
package com.sk89q.wepif;

import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.wepif.PermissionsResolver;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultResolver
implements PermissionsResolver {
    private static Permission perms = null;
    private final Server server;

    public static PermissionsResolver factory(Server server, YAMLProcessor config) {
        if (server.getPluginManager().getPlugin("Vault") == null) {
            return null;
        }
        RegisteredServiceProvider rsp = server.getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return null;
        }
        perms = (Permission)rsp.getProvider();
        if (perms == null) {
            return null;
        }
        return new VaultResolver(server);
    }

    public VaultResolver(Server server) {
        this.server = server;
    }

    @Override
    public void load() {
    }

    @Override
    public String getDetectionMessage() {
        return "Vault detected! Using Vault for permissions";
    }

    @Override
    public boolean hasPermission(String name, String permission) {
        return this.hasPermission(this.server.getOfflinePlayer(name), permission);
    }

    @Override
    public boolean hasPermission(String worldName, String name, String permission) {
        return this.hasPermission(worldName, this.server.getOfflinePlayer(name), permission);
    }

    @Override
    public boolean inGroup(String player, String group) {
        return this.inGroup(this.server.getOfflinePlayer(player), group);
    }

    @Override
    public String[] getGroups(String player) {
        return this.getGroups(this.server.getOfflinePlayer(player));
    }

    @Override
    public boolean hasPermission(OfflinePlayer player, String permission) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return perms.playerHas(null, player, permission);
        }
        return perms.playerHas(onlinePlayer.getWorld().getName(), player, permission);
    }

    @Override
    public boolean hasPermission(String worldName, OfflinePlayer player, String permission) {
        return perms.playerHas(worldName, player, permission);
    }

    @Override
    public boolean inGroup(OfflinePlayer player, String group) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return perms.playerInGroup(null, player, group);
        }
        return perms.playerInGroup(onlinePlayer, group);
    }

    @Override
    public String[] getGroups(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return perms.getPlayerGroups(null, player);
        }
        return perms.getPlayerGroups(onlinePlayer);
    }
}

