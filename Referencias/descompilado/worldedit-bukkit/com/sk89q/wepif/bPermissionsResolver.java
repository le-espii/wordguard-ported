/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.bananaco.bpermissions.api.ApiLayer
 *  de.bananaco.bpermissions.api.util.CalculableType
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 */
package com.sk89q.wepif;

import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.wepif.PermissionsResolver;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class bPermissionsResolver
implements PermissionsResolver {
    private final Server server;

    public static PermissionsResolver factory(Server server, YAMLProcessor config) {
        try {
            Class.forName("de.bananaco.bpermissions.api.ApiLayer");
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        return new bPermissionsResolver(server);
    }

    public bPermissionsResolver(Server server) {
        this.server = server;
    }

    @Override
    public void load() {
    }

    @Override
    public String getDetectionMessage() {
        return "bPermissions detected! Using bPermissions for permissions";
    }

    @Override
    public boolean hasPermission(String name, String permission) {
        return this.hasPermission(this.server.getOfflinePlayer(name), permission);
    }

    @Override
    public boolean hasPermission(String worldName, String name, String permission) {
        return ApiLayer.hasPermission((String)worldName, (CalculableType)CalculableType.USER, (String)name, (String)permission);
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
            return ApiLayer.hasPermission(null, (CalculableType)CalculableType.USER, (String)player.getName(), (String)permission);
        }
        return ApiLayer.hasPermission((String)onlinePlayer.getWorld().getName(), (CalculableType)CalculableType.USER, (String)player.getName(), (String)permission);
    }

    @Override
    public boolean hasPermission(String worldName, OfflinePlayer player, String permission) {
        return this.hasPermission(worldName, player.getName(), permission);
    }

    @Override
    public boolean inGroup(OfflinePlayer player, String group) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return ApiLayer.hasGroupRecursive(null, (CalculableType)CalculableType.USER, (String)player.getName(), (String)group);
        }
        return ApiLayer.hasGroupRecursive((String)onlinePlayer.getWorld().getName(), (CalculableType)CalculableType.USER, (String)player.getName(), (String)group);
    }

    @Override
    public String[] getGroups(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return ApiLayer.getGroups(null, (CalculableType)CalculableType.USER, (String)player.getName());
        }
        return ApiLayer.getGroups((String)onlinePlayer.getWorld().getName(), (CalculableType)CalculableType.USER, (String)player.getName());
    }
}

