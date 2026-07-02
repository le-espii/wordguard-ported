/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.permissions.Permissible
 *  ru.tehkode.permissions.PermissionManager
 *  ru.tehkode.permissions.PermissionUser
 */
package com.sk89q.wepif;

import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.wepif.DinnerPermsResolver;
import com.sk89q.wepif.PermissionsResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.permissions.Permissible;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

public class PermissionsExResolver
extends DinnerPermsResolver {
    private final PermissionManager manager;

    public static PermissionsResolver factory(Server server, YAMLProcessor config) {
        try {
            PermissionManager manager = (PermissionManager)server.getServicesManager().load(PermissionManager.class);
            if (manager == null) {
                return null;
            }
            return new PermissionsExResolver(server, manager);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public PermissionsExResolver(Server server, PermissionManager manager) {
        super(server);
        this.manager = manager;
    }

    @Override
    public boolean hasPermission(String worldName, String name, String permission) {
        return this.manager.has(name, permission, worldName);
    }

    @Override
    public boolean hasPermission(OfflinePlayer player, String permission) {
        Permissible permissible = this.getPermissible(player);
        if (permissible == null) {
            return this.manager.has(player.getUniqueId(), permission, null);
        }
        return permissible.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(String worldName, OfflinePlayer player, String permission) {
        return this.manager.has(player.getUniqueId(), permission, worldName);
    }

    @Override
    public boolean inGroup(OfflinePlayer player, String group) {
        return super.inGroup(player, group) || this.manager.getUser(player.getUniqueId()).inGroup(group);
    }

    @Override
    public String[] getGroups(OfflinePlayer player) {
        if (this.getPermissible(player) == null) {
            PermissionUser user = this.manager.getUser(player.getUniqueId());
            if (user == null) {
                return new String[0];
            }
            return user.getGroupsNames();
        }
        return super.getGroups(player);
    }

    @Override
    public String getDetectionMessage() {
        return "PermissionsEx detected! Using PermissionsEx for permissions.";
    }
}

