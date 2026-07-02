/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 */
package com.sk89q.worldguard.util.profile.cache;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.cache.AbstractProfileCache;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteCache
extends AbstractProfileCache {
    private static final Logger log = Logger.getLogger(SQLiteCache.class.getCanonicalName());
    private final Connection connection;
    private final PreparedStatement updateStatement;

    public SQLiteCache(File file) throws IOException {
        Preconditions.checkNotNull((Object)file);
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        }
        catch (ClassNotFoundException e) {
            throw new IOException("SQLite JDBC support is not installed");
        }
        catch (SQLException e) {
            throw new IOException("Failed to connect to cache file", e);
        }
        try {
            this.createTable();
        }
        catch (SQLException e) {
            throw new IOException("Failed to create tables", e);
        }
        try {
            this.updateStatement = this.connection.prepareStatement("INSERT OR REPLACE INTO uuid_cache (uuid, name) VALUES (?, ?)");
        }
        catch (SQLException e) {
            throw new IOException("Failed to prepare statements", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return this.connection;
    }

    private void createTable() throws SQLException {
        Connection conn = this.getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS uuid_cache (\n  uuid CHAR(36) PRIMARY KEY NOT NULL,\n  name CHAR(32) NOT NULL)");
        try {
            stmt.executeUpdate("CREATE INDEX name_index ON uuid_cache (name)");
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        stmt.close();
    }

    @Override
    public void putAll(Iterable<Profile> entries) {
        try {
            this.executePut(entries);
        }
        catch (SQLException e) {
            log.log(Level.WARNING, "Failed to execute queries", e);
        }
    }

    @Override
    public ImmutableMap<UUID, Profile> getAllPresent(Iterable<UUID> uuids) {
        try {
            return this.executeGet(uuids);
        }
        catch (SQLException e) {
            log.log(Level.WARNING, "Failed to execute queries", e);
            return ImmutableMap.of();
        }
    }

    protected synchronized void executePut(Iterable<Profile> profiles) throws SQLException {
        for (Profile profile : profiles) {
            this.updateStatement.setString(1, profile.getUniqueId().toString());
            this.updateStatement.setString(2, profile.getName());
            this.updateStatement.executeUpdate();
        }
    }

    protected ImmutableMap<UUID, Profile> executeGet(Iterable<UUID> uuids) throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT name, uuid FROM uuid_cache WHERE uuid IN (");
        boolean first = true;
        for (UUID uuid : uuids) {
            Preconditions.checkNotNull((Object)uuid, (Object)"Unexpected null UUID");
            if (!first) {
                builder.append(", ");
            }
            builder.append("'").append(uuid).append("'");
            first = false;
        }
        if (first) {
            return ImmutableMap.of();
        }
        builder.append(")");
        SQLiteCache sQLiteCache = this;
        synchronized (sQLiteCache) {
            ImmutableMap immutableMap;
            block13: {
                Connection conn = this.getConnection();
                Statement stmt = conn.createStatement();
                try {
                    ResultSet rs = stmt.executeQuery(builder.toString());
                    HashMap<UUID, Profile> map = new HashMap<UUID, Profile>();
                    while (rs.next()) {
                        UUID uniqueId = UUID.fromString(rs.getString("uuid"));
                        map.put(uniqueId, new Profile(uniqueId, rs.getString("name")));
                    }
                    immutableMap = ImmutableMap.copyOf(map);
                    if (stmt == null) break block13;
                }
                catch (Throwable throwable) {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                stmt.close();
            }
            return immutableMap;
        }
    }
}

