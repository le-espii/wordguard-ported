/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.HashBasedTable
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 *  org.yaml.snakeyaml.Yaml
 *  org.yaml.snakeyaml.error.YAMLException
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabaseUtils;
import com.sk89q.worldguard.protection.managers.storage.sql.SQLRegionDatabase;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.io.Closer;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

class DataLoader {
    private static final Logger log = Logger.getLogger(DataLoader.class.getCanonicalName());
    final Connection conn;
    final DataSourceConfig config;
    final int worldId;
    final FlagRegistry flagRegistry;
    private final Map<String, ProtectedRegion> loaded = new HashMap<String, ProtectedRegion>();
    private final Map<ProtectedRegion, String> parentSets = new HashMap<ProtectedRegion, String>();
    private final Yaml yaml = SQLRegionDatabase.createYaml();

    DataLoader(SQLRegionDatabase regionStore, Connection conn, FlagRegistry flagRegistry) throws SQLException {
        Preconditions.checkNotNull((Object)regionStore);
        this.conn = conn;
        this.config = regionStore.getDataSourceConfig();
        this.worldId = regionStore.getWorldId();
        this.flagRegistry = flagRegistry;
    }

    public Set<ProtectedRegion> load() throws SQLException {
        this.loadCuboids();
        this.loadPolygons();
        this.loadGlobals();
        this.loadFlags();
        this.loadDomainUsers();
        this.loadDomainGroups();
        RegionDatabaseUtils.relinkParents(this.loaded, this.parentSets);
        return new HashSet<ProtectedRegion>(this.loaded.values());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadCuboids() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("SELECT g.min_z, g.min_y, g.min_x,        g.max_z, g.max_y, g.max_x,        r.id, r.priority, p.id AS parent FROM " + this.config.getTablePrefix() + "region_cuboid AS g LEFT JOIN " + this.config.getTablePrefix() + "region AS r           ON (g.region_id = r.id AND g.world_id = r.world_id) LEFT JOIN " + this.config.getTablePrefix() + "region AS p           ON (r.parent = p.id AND r.world_id = p.world_id) WHERE r.world_id = " + this.worldId));
            ResultSet rs = closer.register(stmt.executeQuery());
            while (rs.next()) {
                BlockVector3 pt1 = BlockVector3.at((int)rs.getInt("min_x"), (int)rs.getInt("min_y"), (int)rs.getInt("min_z"));
                BlockVector3 pt2 = BlockVector3.at((int)rs.getInt("max_x"), (int)rs.getInt("max_y"), (int)rs.getInt("max_z"));
                BlockVector3 min = pt1.getMinimum(pt2);
                BlockVector3 max = pt1.getMaximum(pt2);
                ProtectedCuboidRegion region = new ProtectedCuboidRegion(rs.getString("id"), min, max);
                region.setPriority(rs.getInt("priority"));
                this.loaded.put(rs.getString("id"), region);
                String parentId = rs.getString("parent");
                if (parentId == null) continue;
                this.parentSets.put(region, parentId);
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadGlobals() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("SELECT r.id, r.priority, p.id AS parent FROM " + this.config.getTablePrefix() + "region AS r LEFT JOIN " + this.config.getTablePrefix() + "region AS p           ON (r.parent = p.id AND r.world_id = p.world_id) WHERE r.type = 'global' AND r.world_id = " + this.worldId));
            ResultSet rs = closer.register(stmt.executeQuery());
            while (rs.next()) {
                GlobalProtectedRegion region = new GlobalProtectedRegion(rs.getString("id"));
                region.setPriority(rs.getInt("priority"));
                this.loaded.put(rs.getString("id"), region);
                String parentId = rs.getString("parent");
                if (parentId == null) continue;
                this.parentSets.put(region, parentId);
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadPolygons() throws SQLException {
        ResultSet rs;
        PreparedStatement stmt;
        ArrayListMultimap pointsCache = ArrayListMultimap.create();
        Closer closer = Closer.create();
        try {
            stmt = closer.register(this.conn.prepareStatement("SELECT region_id, x, z FROM " + this.config.getTablePrefix() + "region_poly2d_point WHERE world_id = " + this.worldId));
            rs = closer.register(stmt.executeQuery());
            while (rs.next()) {
                pointsCache.put((Object)rs.getString("region_id"), (Object)BlockVector2.at((int)rs.getInt("x"), (int)rs.getInt("z")));
            }
        }
        finally {
            closer.closeQuietly();
        }
        closer = Closer.create();
        try {
            stmt = closer.register(this.conn.prepareStatement("SELECT g.min_y, g.max_y, r.id, r.priority, p.id AS parent FROM " + this.config.getTablePrefix() + "region_poly2d AS g LEFT JOIN " + this.config.getTablePrefix() + "region AS r           ON (g.region_id = r.id AND g.world_id = r.world_id) LEFT JOIN " + this.config.getTablePrefix() + "region AS p           ON (r.parent = p.id AND r.world_id = p.world_id) WHERE r.world_id = " + this.worldId));
            rs = closer.register(stmt.executeQuery());
            while (rs.next()) {
                String id = rs.getString("id");
                List points = pointsCache.get((Object)id);
                if (points.size() < 3) {
                    log.log(Level.WARNING, "Invalid polygonal region '" + id + "': region has " + points.size() + " point(s) (less than the required 3). Skipping this region.");
                    continue;
                }
                Integer minY = rs.getInt("min_y");
                Integer maxY = rs.getInt("max_y");
                ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(id, points, minY, maxY);
                region.setPriority(rs.getInt("priority"));
                this.loaded.put(id, region);
                String parentId = rs.getString("parent");
                if (parentId == null) continue;
                this.parentSets.put(region, parentId);
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadFlags() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("SELECT region_id, flag, value FROM " + this.config.getTablePrefix() + "region_flag WHERE world_id = " + this.worldId + " AND region_id IN (SELECT id FROM " + this.config.getTablePrefix() + "region WHERE world_id = " + this.worldId + ")"));
            ResultSet rs = closer.register(stmt.executeQuery());
            HashBasedTable data = HashBasedTable.create();
            while (rs.next()) {
                data.put((Object)rs.getString("region_id"), (Object)rs.getString("flag"), this.unmarshalFlagValue(rs.getString("value")));
            }
            for (Map.Entry entry : data.rowMap().entrySet()) {
                ProtectedRegion region = this.loaded.get(entry.getKey());
                region.setFlags(this.flagRegistry.unmarshal((Map)entry.getValue(), true));
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadDomainUsers() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("SELECT p.region_id, u.name, u.uuid, p.owner FROM " + this.config.getTablePrefix() + "region_players AS p LEFT JOIN " + this.config.getTablePrefix() + "user AS u           ON (p.user_id = u.id) WHERE p.world_id = " + this.worldId));
            ResultSet rs = closer.register(stmt.executeQuery());
            while (rs.next()) {
                ProtectedRegion region = this.loaded.get(rs.getString("region_id"));
                if (region == null) continue;
                DefaultDomain domain = rs.getBoolean("owner") ? region.getOwners() : region.getMembers();
                String name = rs.getString("name");
                String uuid = rs.getString("uuid");
                if (name != null) {
                    domain.addPlayer(name);
                    continue;
                }
                if (uuid == null) continue;
                try {
                    domain.addPlayer(UUID.fromString(uuid));
                }
                catch (IllegalArgumentException e) {
                    log.warning("Invalid UUID '" + uuid + "' for region '" + region.getId() + "'");
                }
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadDomainGroups() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("SELECT rg.region_id, g.name, rg.owner FROM `" + this.config.getTablePrefix() + "region_groups` AS rg INNER JOIN `" + this.config.getTablePrefix() + "group` AS g ON (rg.group_id = g.id) AND rg.world_id = " + this.worldId));
            ResultSet rs = closer.register(stmt.executeQuery());
            while (rs.next()) {
                ProtectedRegion region = this.loaded.get(rs.getString("region_id"));
                if (region == null) continue;
                DefaultDomain domain = rs.getBoolean("owner") ? region.getOwners() : region.getMembers();
                domain.addGroup(rs.getString("name"));
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    private Object unmarshalFlagValue(String rawValue) {
        try {
            return this.yaml.load(rawValue);
        }
        catch (YAMLException e) {
            return String.valueOf(rawValue);
        }
    }
}

