/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.extension.platform.Capability
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.regions;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.protection.managers.RegionContainerImpl;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.migration.Migration;
import com.sk89q.worldguard.protection.managers.migration.MigrationException;
import com.sk89q.worldguard.protection.managers.migration.UUIDMigration;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.regions.QueryCache;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nullable;

public abstract class RegionContainer {
    protected final Object lock = new Object();
    protected final QueryCache cache = new QueryCache();
    protected RegionContainerImpl container;

    public void initialize() {
        ConfigurationManager config = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
        this.container = new RegionContainerImpl(config.selectedRegionStoreDriver, WorldGuard.getInstance().getFlagRegistry());
        this.loadWorlds();
        this.autoMigrate();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unload() {
        Object object = this.lock;
        synchronized (object) {
            this.container.unloadAll();
        }
    }

    public RegionDriver getDriver() {
        return this.container.getDriver();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        Object object = this.lock;
        synchronized (object) {
            this.unload();
            this.loadWorlds();
        }
    }

    @Nullable
    public RegionManager get(World world) {
        return this.container.get(world.getName());
    }

    public List<RegionManager> getLoaded() {
        return Collections.unmodifiableList(this.container.getLoaded());
    }

    public Set<RegionManager> getSaveFailures() {
        return this.container.getSaveFailures();
    }

    public RegionQuery createQuery() {
        return new RegionQuery(this.cache);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void migrate(Migration migration) throws MigrationException {
        Preconditions.checkNotNull((Object)migration);
        Object object = this.lock;
        synchronized (object) {
            try {
                WorldGuard.logger.info("Unloading and saving region data that is currently loaded...");
                this.unload();
                migration.migrate();
            }
            finally {
                WorldGuard.logger.info("Loading region data for loaded worlds...");
                this.loadWorlds();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void loadWorlds() {
        WorldGuard.logger.info("Loading region data...");
        Object object = this.lock;
        synchronized (object) {
            for (World world : WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds()) {
                this.load(world);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unload(World world) {
        Preconditions.checkNotNull((Object)world);
        Object object = this.lock;
        synchronized (object) {
            this.container.unload(world.getName());
        }
    }

    protected void autoMigrate() {
        ConfigurationManager config = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
        if (config.migrateRegionsToUuid) {
            RegionDriver driver = this.getDriver();
            UUIDMigration migrator = new UUIDMigration(driver, WorldGuard.getInstance().getProfileService(), WorldGuard.getInstance().getFlagRegistry());
            migrator.setKeepUnresolvedNames(config.keepUnresolvedNames);
            try {
                this.migrate(migrator);
                WorldGuard.logger.info("Regions saved after UUID migration! This won't happen again unless you change the relevant configuration option in WorldGuard's config.");
                config.disableUuidMigration();
            }
            catch (MigrationException e) {
                WorldGuard.logger.log(Level.WARNING, "Failed to execute the migration", e);
            }
        }
    }

    @Nullable
    protected abstract RegionManager load(World var1);
}

