/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.sk89q.worldguard.protection.managers.storage.sql.DataUpdater;
import com.sk89q.worldguard.protection.managers.storage.sql.StatementBatch;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.io.Closer;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class RegionRemover {
    private final DataSourceConfig config;
    private final Connection conn;
    private final int worldId;
    private final List<String> regionQueue = new ArrayList<String>();
    private final List<String> cuboidGeometryQueue = new ArrayList<String>();
    private final List<String> polygonGeometryQueue = new ArrayList<String>();

    RegionRemover(DataUpdater updater) {
        this.config = updater.config;
        this.conn = updater.conn;
        this.worldId = updater.worldId;
    }

    public void removeRegionsEntirely(Collection<String> names) {
        this.regionQueue.addAll(names);
    }

    public void removeGeometry(ProtectedRegion region, String currentType) {
        if (currentType.equals("cuboid")) {
            this.cuboidGeometryQueue.add(region.getId());
        } else if (currentType.equals("poly2d")) {
            this.polygonGeometryQueue.add(region.getId());
        } else if (!currentType.equals("global")) {
            throw new RuntimeException("Unknown type of region in the database: " + currentType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeRows(Collection<String> names, String table, String field) throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("DELETE FROM " + this.config.getTablePrefix() + table + " WHERE " + field + " = ? AND world_id = " + this.worldId));
            StatementBatch batch = new StatementBatch(stmt, 100);
            for (String name : names) {
                stmt.setString(1, name);
                batch.addBatch();
            }
            batch.executeRemaining();
        }
        finally {
            closer.closeQuietly();
        }
    }

    public void apply() throws SQLException {
        this.removeRows(this.regionQueue, "region", "id");
        this.removeRows(this.cuboidGeometryQueue, "region_cuboid", "region_id");
        this.removeRows(this.polygonGeometryQueue, "region_poly2d", "region_id");
    }
}

