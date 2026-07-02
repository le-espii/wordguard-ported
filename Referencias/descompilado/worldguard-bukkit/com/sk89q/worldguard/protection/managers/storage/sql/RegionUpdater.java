/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.yaml.snakeyaml.Yaml
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.google.common.collect.Lists;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.storage.sql.DataUpdater;
import com.sk89q.worldguard.protection.managers.storage.sql.DomainTableCache;
import com.sk89q.worldguard.protection.managers.storage.sql.SQLRegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.sql.StatementBatch;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.io.Closer;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

class RegionUpdater {
    private static final Logger log = Logger.getLogger(RegionUpdater.class.getCanonicalName());
    private final DataSourceConfig config;
    private final Connection conn;
    private final int worldId;
    private final DomainTableCache domainTableCache;
    private final Set<String> userNames = new HashSet<String>();
    private final Set<UUID> userUuids = new HashSet<UUID>();
    private final Set<String> groupNames = new HashSet<String>();
    private final Yaml yaml = SQLRegionDatabase.createYaml();
    private final List<ProtectedRegion> typesToUpdate = new ArrayList<ProtectedRegion>();
    private final List<ProtectedRegion> parentsToSet = new ArrayList<ProtectedRegion>();
    private final List<ProtectedRegion> flagsToReplace = new ArrayList<ProtectedRegion>();
    private final List<ProtectedRegion> domainsToReplace = new ArrayList<ProtectedRegion>();

    RegionUpdater(DataUpdater updater) {
        this.config = updater.config;
        this.conn = updater.conn;
        this.worldId = updater.worldId;
        this.domainTableCache = updater.domainTableCache;
    }

    public void updateRegionType(ProtectedRegion region) {
        this.typesToUpdate.add(region);
    }

    public void updateRegionProperties(ProtectedRegion region) {
        if (region.getParent() != null) {
            this.parentsToSet.add(region);
        }
        this.flagsToReplace.add(region);
        this.domainsToReplace.add(region);
        this.addDomain(region.getOwners());
        this.addDomain(region.getMembers());
    }

    private void addDomain(DefaultDomain domain) {
        for (String name : domain.getPlayers()) {
            this.userNames.add(name.toLowerCase());
        }
        for (UUID uuid : domain.getUniqueIds()) {
            this.userUuids.add(uuid);
        }
        for (String name : domain.getGroups()) {
            this.groupNames.add(name.toLowerCase());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setParents() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("UPDATE " + this.config.getTablePrefix() + "region SET parent = ? WHERE id = ? AND world_id = " + this.worldId));
            for (List partition : Lists.partition(this.parentsToSet, (int)100)) {
                for (ProtectedRegion region : partition) {
                    ProtectedRegion parent = region.getParent();
                    if (parent == null) continue;
                    stmt.setString(1, parent.getId());
                    stmt.setString(2, region.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void replaceFlags() throws SQLException {
        PreparedStatement stmt;
        Closer closer = Closer.create();
        try {
            stmt = closer.register(this.conn.prepareStatement("DELETE FROM " + this.config.getTablePrefix() + "region_flag WHERE region_id = ? AND world_id = " + this.worldId));
            for (List partition : Lists.partition(this.flagsToReplace, (int)100)) {
                for (ProtectedRegion region : partition) {
                    stmt.setString(1, region.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
        finally {
            closer.closeQuietly();
        }
        closer = Closer.create();
        try {
            stmt = closer.register(this.conn.prepareStatement("INSERT INTO " + this.config.getTablePrefix() + "region_flag (id, region_id, world_id, flag, value) VALUES (null, ?, " + this.worldId + ", ?, ?)"));
            StatementBatch batch = new StatementBatch(stmt, 100);
            for (ProtectedRegion region : this.flagsToReplace) {
                for (Map.Entry<Flag<?>, Object> entry : region.getFlags().entrySet()) {
                    if (entry.getValue() == null) continue;
                    Object flag = this.marshalFlagValue(entry.getKey(), entry.getValue());
                    stmt.setString(1, region.getId());
                    stmt.setString(2, entry.getKey().getName());
                    stmt.setObject(3, flag);
                    batch.addBatch();
                }
            }
            batch.executeRemaining();
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void replaceDomainUsers() throws SQLException {
        PreparedStatement stmt;
        Closer closer = Closer.create();
        try {
            stmt = closer.register(this.conn.prepareStatement("DELETE FROM " + this.config.getTablePrefix() + "region_players WHERE region_id = ? AND world_id = " + this.worldId));
            for (List partition : Lists.partition(this.domainsToReplace, (int)100)) {
                for (ProtectedRegion region : partition) {
                    stmt.setString(1, region.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
        finally {
            closer.closeQuietly();
        }
        closer = Closer.create();
        try {
            stmt = closer.register(this.conn.prepareStatement("INSERT INTO " + this.config.getTablePrefix() + "region_players (region_id, world_id, user_id, owner) VALUES (?, " + this.worldId + ",  ?, ?)"));
            StatementBatch batch = new StatementBatch(stmt, 100);
            for (ProtectedRegion region : this.domainsToReplace) {
                this.insertDomainUsers(stmt, batch, region, region.getMembers(), false);
                this.insertDomainUsers(stmt, batch, region, region.getOwners(), true);
            }
            batch.executeRemaining();
        }
        finally {
            closer.closeQuietly();
        }
    }

    private void insertDomainUsers(PreparedStatement stmt, StatementBatch batch, ProtectedRegion region, DefaultDomain domain, boolean owner) throws SQLException {
        Integer id;
        for (String name : domain.getPlayers()) {
            id = this.domainTableCache.getUserNameCache().find(name);
            if (id != null) {
                stmt.setString(1, region.getId());
                stmt.setInt(2, id);
                stmt.setBoolean(3, owner);
                batch.addBatch();
                continue;
            }
            log.log(Level.WARNING, "Did not find an ID for the user identified as '" + name + "'");
        }
        for (UUID uuid : domain.getUniqueIds()) {
            id = this.domainTableCache.getUserUuidCache().find(uuid);
            if (id != null) {
                stmt.setString(1, region.getId());
                stmt.setInt(2, id);
                stmt.setBoolean(3, owner);
                batch.addBatch();
                continue;
            }
            log.log(Level.WARNING, "Did not find an ID for the user identified by '" + String.valueOf(uuid) + "'");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void replaceDomainGroups() throws SQLException {
        PreparedStatement stmt;
        Closer closer = Closer.create();
        try {
            stmt = closer.register(this.conn.prepareStatement("DELETE FROM " + this.config.getTablePrefix() + "region_groups WHERE region_id = ? AND world_id = " + this.worldId));
            for (List partition : Lists.partition(this.domainsToReplace, (int)100)) {
                for (ProtectedRegion region : partition) {
                    stmt.setString(1, region.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
        finally {
            closer.closeQuietly();
        }
        closer = Closer.create();
        try {
            stmt = closer.register(this.conn.prepareStatement("INSERT INTO " + this.config.getTablePrefix() + "region_groups (region_id, world_id, group_id, owner) VALUES (?, " + this.worldId + ",  ?, ?)"));
            StatementBatch batch = new StatementBatch(stmt, 100);
            for (ProtectedRegion region : this.domainsToReplace) {
                this.insertDomainGroups(stmt, batch, region, region.getMembers(), false);
                this.insertDomainGroups(stmt, batch, region, region.getOwners(), true);
            }
            batch.executeRemaining();
        }
        finally {
            closer.closeQuietly();
        }
    }

    private void insertDomainGroups(PreparedStatement stmt, StatementBatch batch, ProtectedRegion region, DefaultDomain domain, boolean owner) throws SQLException {
        for (String name : domain.getGroups()) {
            Integer id = this.domainTableCache.getGroupNameCache().find(name);
            if (id != null) {
                stmt.setString(1, region.getId());
                stmt.setInt(2, id);
                stmt.setBoolean(3, owner);
                batch.addBatch();
                continue;
            }
            log.log(Level.WARNING, "Did not find an ID for the group identified as '" + name + "'");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateRegionTypes() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("UPDATE " + this.config.getTablePrefix() + "region SET type = ?, priority = ?, parent = NULL WHERE id = ? AND world_id = " + this.worldId));
            for (List partition : Lists.partition(this.typesToUpdate, (int)100)) {
                for (ProtectedRegion region : partition) {
                    stmt.setString(1, SQLRegionDatabase.getRegionTypeName(region));
                    stmt.setInt(2, region.getPriority());
                    stmt.setString(3, region.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    public void apply() throws SQLException {
        this.domainTableCache.getUserNameCache().fetch(this.userNames);
        this.domainTableCache.getUserUuidCache().fetch(this.userUuids);
        this.domainTableCache.getGroupNameCache().fetch(this.groupNames);
        this.updateRegionTypes();
        this.setParents();
        this.replaceFlags();
        this.replaceDomainUsers();
        this.replaceDomainGroups();
    }

    private <V> Object marshalFlagValue(Flag<V> flag, Object val) {
        return this.yaml.dump(flag.marshal(val));
    }
}

