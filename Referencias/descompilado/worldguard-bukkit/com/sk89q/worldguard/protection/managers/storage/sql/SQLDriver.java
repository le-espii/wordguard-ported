/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.internal.flywaydb.core.Flyway;
import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.managers.storage.sql.SQLRegionDatabase;
import com.sk89q.worldguard.util.io.Closer;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLDriver
implements RegionDriver {
    private static final Logger log = Logger.getLogger(SQLDriver.class.getCanonicalName());
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final int CONNECTION_TIMEOUT = 6000;
    private final DataSourceConfig config;
    private boolean initialized = false;

    public SQLDriver(DataSourceConfig config) {
        Preconditions.checkNotNull((Object)config);
        this.config = config;
    }

    @Override
    public RegionDatabase get(String name) {
        return new SQLRegionDatabase(this, name);
    }

    @Override
    public List<RegionDatabase> getAll() throws StorageException {
        Closer closer = Closer.create();
        try {
            ArrayList<RegionDatabase> stores = new ArrayList<RegionDatabase>();
            Connection connection = closer.register(this.getConnection());
            Statement stmt = connection.createStatement();
            ResultSet rs = closer.register(stmt.executeQuery("SELECT name FROM " + this.config.getTablePrefix() + "world"));
            while (rs.next()) {
                stores.add(this.get(rs.getString(1)));
            }
            ArrayList<RegionDatabase> arrayList = stores;
            return arrayList;
        }
        catch (SQLException e) {
            throw new StorageException("Failed to fetch list of worlds", e);
        }
        finally {
            closer.closeQuietly();
        }
    }

    synchronized void initialize() throws StorageException {
        if (!this.initialized) {
            try {
                this.migrate();
            }
            catch (SQLException e) {
                throw new StorageException("Failed to migrate database tables", e);
            }
            this.initialized = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void migrate() throws SQLException, StorageException {
        Closer closer = Closer.create();
        Connection conn = closer.register(this.getConnection());
        try {
            boolean hasMigrations;
            boolean isBeforeMigrations;
            boolean isRecent;
            boolean tablesExist;
            try {
                tablesExist = this.tryQuery(conn, "SELECT * FROM " + this.config.getTablePrefix() + "region_cuboid LIMIT 1");
                isRecent = this.tryQuery(conn, "SELECT world_id FROM " + this.config.getTablePrefix() + "region_cuboid LIMIT 1");
                isBeforeMigrations = !this.tryQuery(conn, "SELECT uuid FROM " + this.config.getTablePrefix() + "user LIMIT 1");
                hasMigrations = this.tryQuery(conn, "SELECT * FROM " + this.config.getTablePrefix() + "migrations LIMIT 1");
            }
            finally {
                closer.closeQuietly();
            }
            if (tablesExist && !isRecent) {
                throw new StorageException("Sorry, your tables are too old for the region SQL auto-migration system. Please run region_manual_update_20110325.sql on your database, which comes with WorldGuard or can be found in http://github.com/sk89q/worldguard");
            }
            HashMap<String, String> placeHolders = new HashMap<String, String>();
            placeHolders.put("tablePrefix", this.config.getTablePrefix());
            Flyway flyway = new Flyway();
            if (!hasMigrations) {
                flyway.setInitOnMigrate(true);
                if (tablesExist) {
                    if (isBeforeMigrations) {
                        flyway.setInitVersion(MigrationVersion.fromVersion("1"));
                    }
                    log.log(Level.INFO, "The SQL region tables exist but the migrations table seems to not exist yet. Creating the migrations table...");
                } else {
                    flyway.setInitVersion(MigrationVersion.fromVersion("0"));
                    log.log(Level.INFO, "SQL region tables do not exist: creating...");
                }
            }
            flyway.setClassLoader(this.getClass().getClassLoader());
            flyway.setLocations("migrations/region/" + this.getMigrationFolderName());
            flyway.setDataSource(this.config.getDsn(), this.config.getUsername(), this.config.getPassword(), new String[0]);
            flyway.setTable(this.config.getTablePrefix() + "migrations");
            flyway.setPlaceholders(placeHolders);
            flyway.setValidateOnMigrate(false);
            flyway.migrate();
        }
        catch (FlywayException e) {
            throw new StorageException("Failed to migrate tables", e);
        }
        finally {
            closer.closeQuietly();
        }
    }

    public String getMigrationFolderName() {
        return "mysql";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean tryQuery(Connection conn, String sql) {
        Closer closer = Closer.create();
        try {
            Statement statement = closer.register(conn.createStatement());
            statement.executeQuery(sql);
            boolean bl = true;
            return bl;
        }
        catch (SQLException ex) {
            boolean bl = false;
            return bl;
        }
        finally {
            closer.closeQuietly();
        }
    }

    DataSourceConfig getConfig() {
        return this.config;
    }

    Connection getConnection() throws SQLException {
        Future<Connection> future = EXECUTOR.submit(new Callable<Connection>(){

            @Override
            public Connection call() throws Exception {
                return SQLDriver.this.config.getConnection();
            }
        });
        try {
            return future.get(6000L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            throw new SQLException("Failed to get a SQL connection because the operation was interrupted", e);
        }
        catch (ExecutionException e) {
            throw new SQLException("Failed to get a SQL connection due to an error", e);
        }
        catch (TimeoutException e) {
            future.cancel(true);
            throw new SQLException("Failed to get a SQL connection within the time limit");
        }
    }
}

