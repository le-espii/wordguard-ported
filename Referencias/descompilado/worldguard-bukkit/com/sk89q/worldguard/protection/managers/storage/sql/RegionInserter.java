/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.storage.sql.DataUpdater;
import com.sk89q.worldguard.protection.managers.storage.sql.SQLRegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.sql.StatementBatch;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.io.Closer;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class RegionInserter {
    private final DataSourceConfig config;
    private final Connection conn;
    private final int worldId;
    private final List<ProtectedRegion> all = new ArrayList<ProtectedRegion>();
    private final List<ProtectedCuboidRegion> cuboids = new ArrayList<ProtectedCuboidRegion>();
    private final List<ProtectedPolygonalRegion> polygons = new ArrayList<ProtectedPolygonalRegion>();

    RegionInserter(DataUpdater updater) {
        this.config = updater.config;
        this.conn = updater.conn;
        this.worldId = updater.worldId;
    }

    public void insertRegionType(ProtectedRegion region) throws SQLException {
        this.all.add(region);
    }

    public void insertGeometry(ProtectedRegion region) throws SQLException {
        if (region instanceof ProtectedCuboidRegion) {
            this.cuboids.add((ProtectedCuboidRegion)region);
        } else if (region instanceof ProtectedPolygonalRegion) {
            this.polygons.add((ProtectedPolygonalRegion)region);
        } else if (!(region instanceof GlobalProtectedRegion)) {
            throw new IllegalArgumentException("Unknown type of region: " + region.getClass().getName());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void insertRegionTypes() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("INSERT INTO " + this.config.getTablePrefix() + "region (id, world_id, type, priority, parent) VALUES (?, ?, ?, ?, NULL)"));
            for (List partition : Lists.partition(this.all, (int)100)) {
                for (ProtectedRegion region : partition) {
                    stmt.setString(1, region.getId());
                    stmt.setInt(2, this.worldId);
                    stmt.setString(3, SQLRegionDatabase.getRegionTypeName(region));
                    stmt.setInt(4, region.getPriority());
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
    private void insertCuboids() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("INSERT INTO " + this.config.getTablePrefix() + "region_cuboid (region_id, world_id, min_z, min_y, min_x, max_z, max_y, max_x ) VALUES (?, " + this.worldId + ", ?, ?, ?, ?, ?, ?)"));
            for (List partition : Lists.partition(this.cuboids, (int)100)) {
                for (ProtectedCuboidRegion region : partition) {
                    BlockVector3 min = region.getMinimumPoint();
                    BlockVector3 max = region.getMaximumPoint();
                    stmt.setString(1, region.getId());
                    stmt.setInt(2, min.z());
                    stmt.setInt(3, min.y());
                    stmt.setInt(4, min.x());
                    stmt.setInt(5, max.z());
                    stmt.setInt(6, max.y());
                    stmt.setInt(7, max.x());
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
    private void insertPolygons() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("INSERT INTO " + this.config.getTablePrefix() + "region_poly2d (region_id, world_id, max_y, min_y) VALUES (?, " + this.worldId + ", ?, ?)"));
            for (List partition : Lists.partition(this.polygons, (int)100)) {
                for (ProtectedPolygonalRegion region : partition) {
                    stmt.setString(1, region.getId());
                    stmt.setInt(2, region.getMaximumPoint().y());
                    stmt.setInt(3, region.getMinimumPoint().y());
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
    private void insertPolygonVertices() throws SQLException {
        Closer closer = Closer.create();
        try {
            PreparedStatement stmt = closer.register(this.conn.prepareStatement("INSERT INTO " + this.config.getTablePrefix() + "region_poly2d_point(region_id, world_id, z, x) VALUES (?, " + this.worldId + ", ?, ?)"));
            StatementBatch batch = new StatementBatch(stmt, 100);
            for (ProtectedPolygonalRegion region : this.polygons) {
                for (BlockVector2 point : region.getPoints()) {
                    stmt.setString(1, region.getId());
                    stmt.setInt(2, point.z());
                    stmt.setInt(3, point.x());
                    batch.addBatch();
                }
            }
            batch.executeRemaining();
        }
        finally {
            closer.closeQuietly();
        }
    }

    public void apply() throws SQLException {
        this.insertRegionTypes();
        this.insertCuboids();
        this.insertPolygons();
        this.insertPolygonVertices();
    }
}

