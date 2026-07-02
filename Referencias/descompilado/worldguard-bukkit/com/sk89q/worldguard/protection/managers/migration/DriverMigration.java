/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.managers.migration;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.migration.AbstractMigration;
import com.sk89q.worldguard.protection.managers.migration.MigrationException;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Set;
import java.util.logging.Logger;

public class DriverMigration
extends AbstractMigration {
    private static final Logger log = Logger.getLogger(DriverMigration.class.getCanonicalName());
    private final RegionDriver target;
    private final FlagRegistry flagRegistry;

    public DriverMigration(RegionDriver driver, RegionDriver target, FlagRegistry flagRegistry) {
        super(driver);
        Preconditions.checkNotNull((Object)target);
        Preconditions.checkNotNull((Object)flagRegistry, (Object)"flagRegistry");
        this.target = target;
        this.flagRegistry = flagRegistry;
    }

    @Override
    protected void migrate(RegionDatabase store) throws MigrationException {
        Set<ProtectedRegion> regions;
        log.info("Loading the regions for '" + store.getName() + "' with the old driver...");
        try {
            regions = store.loadAll(this.flagRegistry);
        }
        catch (StorageException e) {
            throw new MigrationException("Failed to load region data for the world '" + store.getName() + "'", e);
        }
        this.write(store.getName(), regions);
    }

    private void write(String name, Set<ProtectedRegion> regions) throws MigrationException {
        log.info("Saving the data for '" + name + "' with the new driver...");
        RegionDatabase store = this.target.get(name);
        try {
            store.saveAll(regions);
        }
        catch (StorageException e) {
            throw new MigrationException("Failed to save region data for '" + store.getName() + "' to the new driver", e);
        }
    }

    @Override
    protected void postMigration() {
    }
}

