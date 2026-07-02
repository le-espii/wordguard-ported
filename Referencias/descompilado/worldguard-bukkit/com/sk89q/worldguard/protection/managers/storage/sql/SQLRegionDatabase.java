/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.yaml.snakeyaml.DumperOptions
 *  org.yaml.snakeyaml.DumperOptions$FlowStyle
 *  org.yaml.snakeyaml.LoaderOptions
 *  org.yaml.snakeyaml.Yaml
 *  org.yaml.snakeyaml.constructor.BaseConstructor
 *  org.yaml.snakeyaml.constructor.SafeConstructor
 *  org.yaml.snakeyaml.representer.Representer
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionDifference;
import com.sk89q.worldguard.protection.managers.storage.DifferenceSaveException;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.managers.storage.sql.DataLoader;
import com.sk89q.worldguard.protection.managers.storage.sql.DataUpdater;
import com.sk89q.worldguard.protection.managers.storage.sql.SQLDriver;
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
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

class SQLRegionDatabase
implements RegionDatabase {
    private final String worldName;
    private final DataSourceConfig config;
    private final SQLDriver driver;
    private int worldId;
    private boolean initialized = false;

    SQLRegionDatabase(SQLDriver driver, String worldName) {
        Preconditions.checkNotNull((Object)driver);
        Preconditions.checkNotNull((Object)worldName);
        this.config = driver.getConfig();
        this.worldName = worldName;
        this.driver = driver;
    }

    @Override
    public String getName() {
        return this.worldName;
    }

    private synchronized void initialize() throws StorageException {
        if (!this.initialized) {
            this.driver.initialize();
            try {
                this.worldId = this.chooseWorldId(this.worldName);
            }
            catch (SQLException e) {
                throw new StorageException("Failed to choose the ID for this world", e);
            }
            this.initialized = true;
        }
    }

    private int chooseWorldId(String worldName) throws SQLException {
        Closer closer = Closer.create();
        try {
            Connection conn = closer.register(this.getConnection());
            PreparedStatement stmt = closer.register(conn.prepareStatement("SELECT id FROM " + this.config.getTablePrefix() + "world WHERE name = ? LIMIT 0, 1"));
            stmt.setString(1, worldName);
            ResultSet worldResult = closer.register(stmt.executeQuery());
            if (worldResult.next()) {
                int n = worldResult.getInt("id");
                return n;
            }
            PreparedStatement stmt2 = closer.register(conn.prepareStatement("INSERT INTO " + this.config.getTablePrefix() + "world  (id, name) VALUES (null, ?)", 1));
            stmt2.setString(1, worldName);
            stmt2.execute();
            ResultSet generatedKeys = stmt2.getGeneratedKeys();
            if (generatedKeys.next()) {
                int n = generatedKeys.getInt(1);
                return n;
            }
            throw new SQLException("Expected result, got none");
        }
        finally {
            closer.closeQuietly();
        }
    }

    private Connection getConnection() throws SQLException {
        return this.driver.getConnection();
    }

    public DataSourceConfig getDataSourceConfig() {
        return this.config;
    }

    public int getWorldId() {
        return this.worldId;
    }

    static String getRegionTypeName(ProtectedRegion region) {
        if (region instanceof ProtectedCuboidRegion) {
            return "cuboid";
        }
        if (region instanceof ProtectedPolygonalRegion) {
            return "poly2d";
        }
        if (region instanceof GlobalProtectedRegion) {
            return "global";
        }
        throw new IllegalArgumentException("Unexpected region type: " + region.getClass().getName());
    }

    static Yaml createYaml() {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        Representer representer = new Representer(options);
        representer.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        return new Yaml((BaseConstructor)new SafeConstructor(new LoaderOptions()), representer, options);
    }

    @Override
    public Set<ProtectedRegion> loadAll(FlagRegistry flagRegistry) throws StorageException {
        this.initialize();
        Closer closer = Closer.create();
        try {
            DataLoader loader;
            try {
                loader = new DataLoader(this, closer.register(this.getConnection()), flagRegistry);
            }
            catch (SQLException e) {
                throw new StorageException("Failed to get a connection to the database", e);
            }
            try {
                Set<ProtectedRegion> e = loader.load();
                return e;
            }
            catch (SQLException e) {
                throw new StorageException("Failed to save the region data to the database", e);
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void saveAll(Set<ProtectedRegion> regions) throws StorageException {
        Preconditions.checkNotNull(regions);
        this.initialize();
        Closer closer = Closer.create();
        try {
            DataUpdater updater;
            try {
                updater = new DataUpdater(this, closer.register(this.getConnection()));
            }
            catch (SQLException e) {
                throw new StorageException("Failed to get a connection to the database", e);
            }
            try {
                updater.saveAll(regions);
            }
            catch (SQLException e) {
                throw new StorageException("Failed to save the region data to the database", e);
            }
        }
        finally {
            closer.closeQuietly();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void saveChanges(RegionDifference difference) throws DifferenceSaveException, StorageException {
        Preconditions.checkNotNull((Object)difference);
        this.initialize();
        Closer closer = Closer.create();
        try {
            DataUpdater updater;
            try {
                updater = new DataUpdater(this, closer.register(this.getConnection()));
            }
            catch (SQLException e) {
                throw new StorageException("Failed to get a connection to the database", e);
            }
            try {
                updater.saveChanges(difference.getChanged(), difference.getRemoved());
            }
            catch (SQLException e) {
                throw new StorageException("Failed to save the region data to the database", e);
            }
        }
        finally {
            closer.closeQuietly();
        }
    }
}

