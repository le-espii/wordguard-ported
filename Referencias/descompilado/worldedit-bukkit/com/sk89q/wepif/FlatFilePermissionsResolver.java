/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 */
package com.sk89q.wepif;

import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.wepif.PermissionsResolver;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class FlatFilePermissionsResolver
implements PermissionsResolver {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private Map<String, Set<String>> userPermissionsCache;
    private Set<String> defaultPermissionsCache;
    private Map<String, Set<String>> userGroups;
    private final File groupFile;
    private final File userFile;

    public static PermissionsResolver factory(Server server, YAMLProcessor config) {
        File groups = new File("perms_groups.txt");
        File users = new File("perms_users.txt");
        if (!groups.exists() || !users.exists()) {
            return null;
        }
        return new FlatFilePermissionsResolver(groups, users);
    }

    public FlatFilePermissionsResolver() {
        this(new File("perms_groups.txt"), new File("perms_users.txt"));
    }

    public FlatFilePermissionsResolver(File groupFile, File userFile) {
        this.groupFile = groupFile;
        this.userFile = userFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Set<String>> loadGroupPermissions() {
        HashMap<String, Set<String>> userGroupPermissions = new HashMap<String, Set<String>>();
        BufferedReader buff = null;
        try {
            String line;
            FileReader input = new FileReader(this.groupFile);
            buff = new BufferedReader(input);
            while ((line = buff.readLine()) != null) {
                if ((line = line.trim()).isEmpty() || line.charAt(0) == ';' || line.charAt(0) == '#') continue;
                String[] parts = line.split(":");
                String key = parts[0];
                if (parts.length <= 1) continue;
                String[] perms = parts[1].split(",");
                HashSet<String> groupPerms = new HashSet<String>(Arrays.asList(perms));
                userGroupPermissions.put(key, groupPerms);
            }
        }
        catch (IOException e) {
            LOGGER.warn("Failed to load permissions", (Throwable)e);
        }
        finally {
            try {
                if (buff != null) {
                    buff.close();
                }
            }
            catch (IOException iOException) {}
        }
        return userGroupPermissions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void load() {
        this.userGroups = new HashMap<String, Set<String>>();
        this.userPermissionsCache = new HashMap<String, Set<String>>();
        this.defaultPermissionsCache = new HashSet<String>();
        Map<String, Set<String>> userGroupPermissions = this.loadGroupPermissions();
        if (userGroupPermissions.containsKey("default")) {
            this.defaultPermissionsCache = userGroupPermissions.get("default");
        }
        BufferedReader buff = null;
        try {
            String line;
            FileReader input = new FileReader(this.userFile);
            buff = new BufferedReader(input);
            while ((line = buff.readLine()) != null) {
                HashSet<String> permsCache = new HashSet<String>();
                if ((line = line.trim()).isEmpty() || line.charAt(0) == ';' || line.charAt(0) == '#') continue;
                String[] parts = line.split(":");
                String key = parts[0];
                if (parts.length <= 1) continue;
                String[] groups = (parts[1] + ",default").split(",");
                String[] perms = parts.length > 2 ? parts[2].split(",") : new String[]{};
                permsCache.addAll(Arrays.asList(perms));
                for (String group : groups) {
                    Set<String> groupPerms = userGroupPermissions.get(group);
                    if (groupPerms == null) continue;
                    permsCache.addAll(groupPerms);
                }
                this.userPermissionsCache.put(key.toLowerCase(Locale.ROOT), permsCache);
                this.userGroups.put(key.toLowerCase(Locale.ROOT), new HashSet<String>(Arrays.asList(groups)));
            }
        }
        catch (IOException e) {
            LOGGER.warn("Failed to load permissions", (Throwable)e);
        }
        finally {
            try {
                if (buff != null) {
                    buff.close();
                }
            }
            catch (IOException iOException) {}
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
        return groups != null && groups.contains(group);
    }

    @Override
    public String[] getGroups(String player) {
        Set<String> groups = this.userGroups.get(player.toLowerCase(Locale.ROOT));
        if (groups == null) {
            return new String[0];
        }
        return groups.toArray(new String[groups.size()]);
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
        return "perms_groups.txt and perms_users.txt detected! Using flat file permissions.";
    }
}

