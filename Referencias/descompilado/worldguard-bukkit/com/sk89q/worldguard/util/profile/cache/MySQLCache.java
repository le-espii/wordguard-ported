/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 */
package com.sk89q.worldguard.util.profile.cache;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.cache.AbstractProfileCache;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public class MySQLCache
extends AbstractProfileCache {
    public static final String TABLE_NAME = "uuid_cache";
    private static final Logger log = Logger.getLogger(MySQLCache.class.getCanonicalName());
    private final String tableName;
    private final String queryString;
    private DataSource dataSource;
    private Connection connection;

    private MySQLCache(@Nonnull Object o, @Nonnull String tableName) throws SQLException {
        Preconditions.checkNotNull((Object)o);
        Preconditions.checkNotNull((Object)tableName, (Object)"tableName cannot be null.");
        Preconditions.checkArgument((!tableName.isEmpty() ? 1 : 0) != 0, (Object)"tableName cannot be empty.");
        if (o instanceof Connection) {
            this.connection = (Connection)o;
        } else {
            this.dataSource = (DataSource)o;
        }
        this.tableName = tableName;
        this.queryString = "REPLACE INTO `" + tableName + "` (uuid, name) VALUES (?, ?)";
        this.createTable();
    }

    public static MySQLCache create(@Nonnull DataSource dataSource) throws SQLException {
        Preconditions.checkNotNull((Object)dataSource, (Object)"dataSource cannot be null.");
        return new MySQLCache(dataSource, TABLE_NAME);
    }

    public static MySQLCache create(@Nonnull DataSource dataSource, @Nonnull String tableName) throws SQLException {
        Preconditions.checkNotNull((Object)dataSource, (Object)"dataSource cannot be null.");
        return new MySQLCache(dataSource, tableName);
    }

    public static MySQLCache create(@Nonnull Connection connection) throws SQLException {
        Preconditions.checkNotNull((Object)connection, (Object)"connection cannot be null.");
        return new MySQLCache(connection, TABLE_NAME);
    }

    public static MySQLCache create(@Nonnull Connection connection, @Nonnull String tableName) throws SQLException {
        Preconditions.checkNotNull((Object)connection, (Object)"connection cannot be null.");
        return new MySQLCache(connection, tableName);
    }

    @Override
    public void putAll(Iterable<Profile> profiles) {
        try {
            this.executePut(profiles);
        }
        catch (SQLException e) {
            log.log(Level.WARNING, "Failed to execute queries", e);
        }
    }

    @Override
    public ImmutableMap<UUID, Profile> getAllPresent(Iterable<UUID> ids) {
        try {
            return this.executeGet(ids);
        }
        catch (SQLException e) {
            log.log(Level.WARNING, "Failed to execute queries", e);
            return ImmutableMap.of();
        }
    }

    public void createTable() throws SQLException {
        Connection conn = this.getConnection();
        try (Statement stmt = conn.createStatement();){
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.tableName + "` (`uuid` CHAR(36) PRIMARY KEY NOT NULL, `name` VARCHAR(16) NOT NULL UNIQUE KEY)");
        }
        catch (SQLException e) {
            throw new SQLException("Failed to create table.", e);
        }
        finally {
            this.close(conn);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void executePut(Iterable<Profile> profiles) throws SQLException {
        Connection conn = this.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(this.queryString);){
            for (Profile profile : profiles) {
                stmt.setString(1, profile.getUniqueId().toString());
                stmt.setString(2, profile.getName());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
        finally {
            this.close(conn);
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    protected ImmutableMap<UUID, Profile> executeGet(Iterable<UUID> ids) throws SQLException {
        Iterator<UUID> it = ids.iterator();
        if (!it.hasNext()) {
            return ImmutableMap.of();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT name, uuid FROM `").append(this.tableName).append("` WHERE uuid IN ('");
        Joiner.on((String)"', '").skipNulls().appendTo(builder, ids);
        builder.append("');");
        MySQLCache mySQLCache = this;
        synchronized (mySQLCache) {
            Connection conn = this.getConnection();
            try {
                ImmutableMap immutableMap;
                block14: {
                    Statement stmt = conn.createStatement();
                    try {
                        ResultSet rs = stmt.executeQuery(builder.toString());
                        HashMap<UUID, Profile> map = new HashMap<UUID, Profile>();
                        while (rs.next()) {
                            UUID uuid = UUID.fromString(rs.getString("uuid"));
                            map.put(uuid, new Profile(uuid, rs.getString("name")));
                        }
                        immutableMap = ImmutableMap.copyOf(map);
                        if (stmt == null) break block14;
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
            finally {
                this.close(conn);
            }
        }
    }

    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return this.dataSource.getConnection();
    }

    private void close(Connection connection) throws SQLException {
        if (this.dataSource != null) {
            connection.close();
        }
    }

    @Nonnull
    public String getTableName() {
        return this.tableName;
    }
}

