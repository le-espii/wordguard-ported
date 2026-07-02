/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.managers.storage.sql.DomainTableCache;
import com.sk89q.worldguard.protection.managers.storage.sql.RegionInserter;
import com.sk89q.worldguard.protection.managers.storage.sql.RegionRemover;
import com.sk89q.worldguard.protection.managers.storage.sql.RegionUpdater;
import com.sk89q.worldguard.protection.managers.storage.sql.SQLRegionDatabase;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.io.Closer;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

class DataUpdater {
    final Connection conn;
    final DataSourceConfig config;
    final int worldId;
    final DomainTableCache domainTableCache;

    DataUpdater(SQLRegionDatabase regionStore, Connection conn) throws SQLException {
        Preconditions.checkNotNull((Object)regionStore);
        this.conn = conn;
        this.config = regionStore.getDataSourceConfig();
        this.worldId = regionStore.getWorldId();
        this.domainTableCache = new DomainTableCache(this.config, conn);
    }

    public void saveAll(Set<ProtectedRegion> regions) throws SQLException {
        this.executeSave(regions, null);
    }

    public void saveChanges(Set<ProtectedRegion> changed, Set<ProtectedRegion> removed) throws SQLException {
        this.executeSave(changed, removed);
    }

    private void executeSave(Set<ProtectedRegion> toUpdate, @Nullable Set<ProtectedRegion> toRemove) throws SQLException {
        Map<String, String> existing = this.getExistingRegions();
        try {
            this.conn.setAutoCommit(false);
            RegionUpdater updater = new RegionUpdater(this);
            RegionInserter inserter = new RegionInserter(this);
            RegionRemover remover = new RegionRemover(this);
            for (ProtectedRegion region : toUpdate) {
                if (toRemove != null && toRemove.contains(region)) continue;
                String currentType = existing.get(region.getId());
                if (currentType != null) {
                    existing.remove(region.getId());
                    updater.updateRegionType(region);
                    remover.removeGeometry(region, currentType);
                } else {
                    inserter.insertRegionType(region);
                }
                inserter.insertGeometry(region);
                updater.updateRegionProperties(region);
            }
            if (toRemove != null) {
                ArrayList<String> removeNames = new ArrayList<String>();
                for (ProtectedRegion region : toRemove) {
                    removeNames.add(region.getId());
                }
                remover.removeRegionsEntirely(removeNames);
            } else {
                remover.removeRegionsEntirely(existing.keySet());
            }
            remover.apply();
            inserter.apply();
            updater.apply();
            this.conn.commit();
        }
        catch (SQLException e) {
            this.conn.rollback();
            throw e;
        }
        catch (RuntimeException e) {
            this.conn.rollback();
            throw e;
        }
        finally {
            this.conn.setAutoCommit(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, String> getExistingRegions() throws SQLException {
        HashMap<String, String> existing = new HashMap<String, String>();
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("SELECT id, type FROM " + this.config.getTablePrefix() + "region WHERE world_id = " + this.worldId));
            ResultSet resultSet = closer.register(stmt.executeQuery());
            while (resultSet.next()) {
                existing.put(resultSet.getString("id"), resultSet.getString("type"));
            }
            HashMap<String, String> hashMap = existing;
            return hashMap;
        }
        finally {
            closer.closeQuietly();
        }
    }
}

