/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nijikokun.bukkit.Permissions.Permissions
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 */
package com.sk89q.wepif;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.wepif.PermissionsResolver;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class NijiPermissionsResolver
implements PermissionsResolver {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final Server server;
    private final Permissions api;

    public static PermissionsResolver factory(Server server, YAMLProcessor config) {
        PluginManager pluginManager = server.getPluginManager();
        try {
            Class.forName("com.nijikokun.bukkit.Permissions.Permissions");
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        Plugin plugin = pluginManager.getPlugin("Permissions");
        if (!(plugin instanceof Permissions)) {
            return null;
        }
        if (config.getBoolean("ignore-nijiperms-bridges", true) && NijiPermissionsResolver.isFakeNijiPerms(plugin)) {
            return null;
        }
        return new NijiPermissionsResolver(server, (Permissions)plugin);
    }

    @Override
    public void load() {
    }

    public NijiPermissionsResolver(Server server, Permissions plugin) {
        this.server = server;
        this.api = plugin;
    }

    /*
     * Loose catch block
     */
    @Override
    public boolean hasPermission(String name, String permission) {
        Player player;
        block5: {
            player = this.server.getPlayerExact(name);
            if (player != null) break block5;
            return false;
            {
                catch (Throwable t) {
                    LOGGER.warn("Failed to check permissions", t);
                    return false;
                }
            }
        }
        try {
            return this.api.getHandler().has(player, permission);
        }
        catch (Throwable t) {
            return Permissions.Security.permission(player, permission);
        }
    }

    @Override
    public boolean hasPermission(String worldName, String name, String permission) {
        try {
            return this.api.getHandler().has(worldName, name, permission);
        }
        catch (Throwable t) {
            try {
                return this.api.getHandler().has(this.server.getPlayerExact(name), permission);
            }
            catch (Throwable t2) {
                LOGGER.warn("Failed to check permissions", t2);
                return false;
            }
        }
    }

    /*
     * Loose catch block
     */
    @Override
    public boolean inGroup(String name, String group) {
        Player player;
        block5: {
            player = this.server.getPlayerExact(name);
            if (player != null) break block5;
            return false;
            {
                catch (Throwable t) {
                    LOGGER.warn("Failed to check groups", t);
                    return false;
                }
            }
        }
        try {
            return this.api.getHandler().inGroup(player.getWorld().getName(), name, group);
        }
        catch (Throwable t) {
            return Permissions.Security.inGroup(name, group);
        }
    }

    @Override
    public String[] getGroups(String name) {
        try {
            String[] groups;
            block6: {
                Player player = this.server.getPlayerExact(name);
                if (player == null) {
                    return new String[0];
                }
                groups = null;
                try {
                    groups = this.api.getHandler().getGroups(player.getWorld().getName(), player.getName());
                }
                catch (Throwable t) {
                    String group = Permissions.Security.getGroup(player.getWorld().getName(), player.getName());
                    if (group == null) break block6;
                    groups = new String[]{group};
                }
            }
            if (groups == null) {
                return new String[0];
            }
            return groups;
        }
        catch (Throwable t) {
            LOGGER.warn("Failed to get groups", t);
            return new String[0];
        }
    }

    @Override
    public boolean hasPermission(OfflinePlayer player, String permission) {
        return this.hasPermission(player.getName(), permission);
    }

    @Override
    public boolean hasPermission(String worldName, OfflinePlayer player, String permission) {
        return this.hasPermission(worldName, player.getName(), permission);
    }

    @Override
    public boolean inGroup(OfflinePlayer player, String group) {
        return this.inGroup(player.getName(), group);
    }

    @Override
    public String[] getGroups(OfflinePlayer player) {
        return this.getGroups(player.getName());
    }

    public static boolean isFakeNijiPerms(Plugin plugin) {
        PluginCommand permsCommand = Bukkit.getServer().getPluginCommand("permissions");
        return permsCommand == null || !permsCommand.getPlugin().equals((Object)plugin);
    }

    @Override
    public String getDetectionMessage() {
        return "Permissions plugin detected! Using Permissions plugin for permissions.";
    }
}

