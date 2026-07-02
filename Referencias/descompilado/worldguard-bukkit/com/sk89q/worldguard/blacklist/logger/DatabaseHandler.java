/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.blacklist.logger;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import com.sk89q.worldguard.blacklist.event.EventType;
import com.sk89q.worldguard.blacklist.logger.LoggerHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

public class DatabaseHandler
implements LoggerHandler {
    private final String dsn;
    private final String user;
    private final String pass;
    private final String table;
    private final String worldName;
    private Connection conn;
    private final Logger logger;

    public DatabaseHandler(String dsn, String user, String pass, String table, String worldName, Logger logger) {
        this.dsn = dsn;
        this.user = user;
        this.pass = pass;
        this.table = table;
        this.worldName = worldName;
        this.logger = logger;
    }

    private Connection getConnection() throws SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            this.conn = DriverManager.getConnection(this.dsn, this.user, this.pass);
        }
        return this.conn;
    }

    private void logEvent(EventType eventType, @Nullable LocalPlayer player, BlockVector3 pos, String item, String comment) {
        try {
            Connection conn = this.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + this.table + "(event, world, player, x, y, z, item, time, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, eventType.name());
            stmt.setString(2, this.worldName);
            stmt.setString(3, player != null ? player.getName() : "");
            stmt.setInt(4, pos.x());
            stmt.setInt(5, pos.y());
            stmt.setInt(6, pos.z());
            stmt.setString(7, item);
            stmt.setInt(8, (int)(System.currentTimeMillis() / 1000L));
            stmt.setString(9, comment);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            this.logger.log(Level.SEVERE, "Failed to log blacklist event to database: " + e.getMessage());
        }
    }

    @Override
    public void logEvent(BlacklistEvent event, String comment) {
        this.logEvent(event.getEventType(), event.getPlayer(), event.getLoggedPosition(), event.getTarget().getTypeId(), comment);
    }

    @Override
    public void close() {
        try {
            if (this.conn != null && !this.conn.isClosed()) {
                this.conn.close();
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }
}

