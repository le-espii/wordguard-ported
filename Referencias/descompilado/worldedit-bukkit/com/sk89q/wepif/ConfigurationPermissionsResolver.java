/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 */
package com.sk89q.wepif;

import com.sk89q.util.yaml.YAMLNode;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.wepif.PermissionsResolver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.bukkit.OfflinePlayer;

public class ConfigurationPermissionsResolver
implements PermissionsResolver {
    private final YAMLProcessor config;
    private final Map<String, Set<String>> userPermissionsCache = new HashMap<String, Set<String>>();
    private final Set<String> defaultPermissionsCache = new HashSet<String>();
    private final Map<String, Set<String>> userGroups = new HashMap<String, Set<String>>();

    public ConfigurationPermissionsResolver(YAMLProcessor config) {
        this.config = config;
    }

    public static YAMLNode generateDefaultPerms(YAMLNode section) {
        section.setProperty("groups.default.permissions", new String[]{"worldedit.reload", "worldedit.selection", "worlds.creative.worldedit.region"});
        section.setProperty("groups.admins.permissions", new String[]{"*"});
        section.setProperty("users.sk89q.permissions", new String[]{"worldedit"});
        section.setProperty("users.sk89q.groups", new String[]{"admins"});
        return section;
    }

    @Override
    public void load() {
        List<String> userKeys;
        this.userGroups.clear();
        this.userPermissionsCache.clear();
        this.defaultPermissionsCache.clear();
        HashMap<String, HashSet<String>> userGroupPermissions = new HashMap<String, HashSet<String>>();
        List<String> groupKeys = this.config.getKeys("permissions.groups");
        if (groupKeys != null) {
            for (String key : groupKeys) {
                List<String> permissions = this.config.getStringList("permissions.groups." + key + ".permissions", null);
                if (permissions.isEmpty()) continue;
                HashSet<String> groupPerms = new HashSet<String>(permissions);
                userGroupPermissions.put(key, groupPerms);
                if (!key.equals("default")) continue;
                this.defaultPermissionsCache.addAll(permissions);
            }
        }
        if ((userKeys = this.config.getKeys("permissions.users")) != null) {
            for (String key : userKeys) {
                HashSet<String> permsCache = new HashSet<String>();
                List<String> permissions = this.config.getStringList("permissions.users." + key + ".permissions", null);
                if (!permissions.isEmpty()) {
                    permsCache.addAll(permissions);
                }
                List<String> groups = this.config.getStringList("permissions.users." + key + ".groups", null);
                groups.add("default");
                if (!groups.isEmpty()) {
                    for (String group : groups) {
                        Set groupPerms = (Set)userGroupPermissions.get(group);
                        if (groupPerms == null) continue;
                        permsCache.addAll(groupPerms);
                    }
                }
                this.userPermissionsCache.put(key.toLowerCase(Locale.ROOT), permsCache);
                this.userGroups.put(key.toLowerCase(Locale.ROOT), new HashSet<String>(groups));
            }
        }
    }

    @Override
    public boolean hasPermission(String player, String permission) {
        int dotPos = permission.lastIndexOf(".");
        if (dotPos > -1 && this.hasPermission(player, permission.substring(0, dotPos))) {
            return true;
        }
        Set<String> perms = this.userPermissionsCache.get(player.toLowerCase(Locale.ROOT));
        if (perms == null) {
            return this.defaultPermissionsCache.contains(permission) || this.defaultPermissionsCache.contains("*");
        }
        return perms.contains("*") || perms.contains(permission);
    }

    @Override
    public boolean hasPermission(String worldName, String player, String permission) {
        return this.hasPermission(player, "worlds." + worldName + "." + permission) || this.hasPermission(player, permission);
    }

    @Override
    public boolean inGroup(String player, String group) {
        Set<String> groups = this.userGroups.get(player.toLowerCase(Locale.ROOT));
        if (groups == null) {
            return false;
        }
        return groups.contains(group);
    }

    @Override
    public String[] getGroups(String player) {
        Set<String> groups = this.userGroups.get(player.toLowerCase(Locale.ROOT));
        if (groups == null) {
            return new String[0];
        }
        return groups.toArray(new String[0]);
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

    @Override
    public String getDetectionMessage() {
        return "No known permissions plugin detected. Using configuration file for permissions.";
    }
}

