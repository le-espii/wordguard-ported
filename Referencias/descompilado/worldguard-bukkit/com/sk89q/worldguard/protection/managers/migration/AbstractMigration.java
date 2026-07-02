/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.managers.migration;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.managers.migration.Migration;
import com.sk89q.worldguard.protection.managers.migration.MigrationException;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractMigration
implements Migration {
    private static final Logger log = Logger.getLogger(AbstractMigration.class.getCanonicalName());
    private final RegionDriver driver;

    public AbstractMigration(RegionDriver driver) {
        Preconditions.checkNotNull((Object)driver);
        this.driver = driver;
    }

    @Override
    public final void migrate() throws MigrationException {
        try {
            for (RegionDatabase store : this.driver.getAll()) {
                try {
                    this.migrate(store);
                }
                catch (MigrationException e) {
                    log.log(Level.WARNING, "Migration of one world (" + store.getName() + ") failed with an error", e);
                }
            }
            this.postMigration();
        }
        catch (StorageException e) {
            throw new MigrationException("Migration failed because the process of getting a list of all the worlds to migrate failed", e);
        }
    }

    protected abstract void migrate(RegionDatabase var1) throws MigrationException;

    protected abstract void postMigration();
}

