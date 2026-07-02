/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.util.sql;

import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceConfig {
    private final String dsn;
    private final String username;
    private final String password;
    private final String tablePrefix;

    public DataSourceConfig(String dsn, String username, String password, String tablePrefix) {
        Preconditions.checkNotNull((Object)dsn);
        Preconditions.checkNotNull((Object)username);
        Preconditions.checkNotNull((Object)password);
        Preconditions.checkNotNull((Object)tablePrefix);
        this.dsn = dsn;
        this.username = username;
        this.password = password;
        this.tablePrefix = tablePrefix;
    }

    public String getDsn() {
        return this.dsn;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getTablePrefix() {
        return this.tablePrefix;
    }

    public DataSourceConfig setDsn(String dsn) {
        return new DataSourceConfig(dsn, this.username, this.password, this.tablePrefix);
    }

    public DataSourceConfig setUsername(String username) {
        return new DataSourceConfig(this.dsn, username, this.password, this.tablePrefix);
    }

    public DataSourceConfig setPassword(String password) {
        return new DataSourceConfig(this.dsn, this.username, password, this.tablePrefix);
    }

    public DataSourceConfig setTablePrefix(String tablePrefix) {
        return new DataSourceConfig(this.dsn, this.username, this.password, tablePrefix);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.dsn, this.username, this.password);
    }
}

