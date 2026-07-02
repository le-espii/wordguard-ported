/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.managers.migration;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.migration.AbstractMigration;
import com.sk89q.worldguard.protection.managers.migration.MigrationException;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

public class WorldHeightMigration
extends AbstractMigration {
    private static final Logger log = Logger.getLogger(WorldHeightMigration.class.getCanonicalName());
    private static final Field minField;
    private static final Field maxField;
    private final FlagRegistry flagRegistry;
    private final World world;
    private int changed = 0;

    public WorldHeightMigration(RegionDriver driver, FlagRegistry flagRegistry, @Nullable World world) {
        super(driver);
        this.flagRegistry = flagRegistry;
        this.world = world;
    }

    @Override
    protected void migrate(RegionDatabase store) throws MigrationException {
        Set<ProtectedRegion> regions;
        if (this.world != null && !store.getName().equals(this.world.getName())) {
            return;
        }
        log.log(Level.INFO, "Migrating regions in '" + store.getName() + "' to new height limits...");
        try {
            regions = store.loadAll(this.flagRegistry);
        }
        catch (StorageException e) {
            throw new MigrationException("Failed to load region data for the world '" + store.getName() + "'", e);
        }
        int min = -64;
        int max = 319;
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(store.getName());
        if (world != null) {
            min = world.getMinY();
            max = world.getMaxY();
            if (min == 0 && max == 255) {
                return;
            }
        }
        for (ProtectedRegion region : regions) {
            if (region.getMinimumPoint().y() > 0 || region.getMaximumPoint().y() < 255) continue;
            WorldHeightMigration.expand(region, min, max);
            ++this.changed;
        }
        try {
            store.saveAll(regions);
        }
        catch (StorageException e) {
            throw new MigrationException("Failed to save region data after migration of the world '" + store.getName() + "'", e);
        }
    }

    private static void expand(ProtectedRegion region, int min, int max) throws MigrationException {
        try {
            minField.set(region, region.getMinimumPoint().withY(min));
            maxField.set(region, region.getMaximumPoint().withY(max));
            region.setDirty(true);
        }
        catch (IllegalAccessException e) {
            throw new MigrationException("Migrator broke.", e);
        }
    }

    @Override
    protected void postMigration() {
        log.log(Level.INFO, "A total of " + this.changed + " top-to-bottom regions were vertically expanded.");
    }

    static {
        try {
            minField = ProtectedRegion.class.getDeclaredField("min");
            minField.setAccessible(true);
            maxField = ProtectedRegion.class.getDeclaredField("max");
            maxField.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(new MigrationException("Migrator broke.", e));
        }
    }
}

