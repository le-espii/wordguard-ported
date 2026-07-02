/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 */
package com.sk89q.worldguard.protection.managers.migration;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.domains.PlayerDomain;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.migration.AbstractMigration;
import com.sk89q.worldguard.protection.managers.migration.MigrationException;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.RegionDriver;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UUIDMigration
extends AbstractMigration {
    private static final Logger log = Logger.getLogger(UUIDMigration.class.getCanonicalName());
    private static final int LOG_DELAY = 5000;
    private final Timer timer = new Timer("WorldGuard UUID Migration");
    private final ProfileService profileService;
    private final FlagRegistry flagRegistry;
    private final ConcurrentMap<String, UUID> resolvedNames = new ConcurrentHashMap<String, UUID>();
    private final Set<String> unresolvedNames = new HashSet<String>();
    private boolean keepUnresolvedNames = true;

    public UUIDMigration(RegionDriver driver, ProfileService profileService, FlagRegistry flagRegistry) {
        super(driver);
        Preconditions.checkNotNull((Object)profileService);
        Preconditions.checkNotNull((Object)flagRegistry, (Object)"flagRegistry");
        this.profileService = profileService;
        this.flagRegistry = flagRegistry;
    }

    @Override
    protected void migrate(RegionDatabase store) throws MigrationException {
        Set<ProtectedRegion> regions;
        log.log(Level.INFO, "Migrating regions in '" + store.getName() + "' to convert names -> UUIDs...");
        try {
            regions = store.loadAll(this.flagRegistry);
        }
        catch (StorageException e) {
            throw new MigrationException("Failed to load region data for the world '" + store.getName() + "'", e);
        }
        this.migrate(regions);
        try {
            store.saveAll(regions);
        }
        catch (StorageException e) {
            throw new MigrationException("Failed to save region data after migration of the world '" + store.getName() + "'", e);
        }
    }

    private boolean migrate(Collection<ProtectedRegion> regions) throws MigrationException {
        Set<String> names = UUIDMigration.getNames(regions);
        if (!names.isEmpty()) {
            ResolvedNamesTimerTask task = new ResolvedNamesTimerTask();
            try {
                this.timer.schedule((TimerTask)task, 5000L, 5000L);
                log.log(Level.INFO, "Resolving " + names.size() + " name(s) into UUIDs... this may take a while.");
                HashSet<String> lookupNames = new HashSet<String>(names);
                lookupNames.removeAll(this.resolvedNames.keySet());
                this.profileService.findAllByName(lookupNames, (Predicate<Profile>)new com.google.common.base.Predicate<Profile>(){

                    public boolean apply(Profile profile) {
                        UUIDMigration.this.resolvedNames.put(profile.getName().toLowerCase(), profile.getUniqueId());
                        return true;
                    }
                });
            }
            catch (IOException e) {
                throw new MigrationException("The name -> UUID service failed", e);
            }
            catch (InterruptedException e) {
                throw new MigrationException("The migration was interrupted");
            }
            finally {
                task.cancel();
            }
            log.log(Level.INFO, "UUIDs resolved... now migrating all regions to UUIDs where possible...");
            this.convert(regions);
            return true;
        }
        return false;
    }

    @Override
    protected void postMigration() {
        if (!this.unresolvedNames.isEmpty()) {
            if (this.keepUnresolvedNames) {
                log.log(Level.WARNING, "Some member and owner names do not seem to exist or own Minecraft so they could not be converted into UUIDs. They have been left as names, but the conversion can be re-run with 'keep-names-that-lack-uuids' set to false in the configuration in order to remove these names. Leaving the names means that someone can register with one of these names in the future and become that player.");
            } else {
                log.log(Level.WARNING, "Some member and owner names do not seem to exist or own Minecraft so they could not be converted into UUIDs. These names have been removed.");
            }
        }
    }

    private static Set<String> getNames(Collection<ProtectedRegion> regions) {
        HashSet<String> names = new HashSet<String>();
        for (ProtectedRegion region : regions) {
            names.addAll(region.getOwners().getPlayers());
            names.addAll(region.getMembers().getPlayers());
        }
        return names;
    }

    private void convert(Collection<ProtectedRegion> regions) {
        for (ProtectedRegion region : regions) {
            this.convert(region.getOwners());
            this.convert(region.getMembers());
        }
    }

    private void convert(DefaultDomain domain) {
        PlayerDomain playerDomain = new PlayerDomain();
        for (UUID uuid : domain.getUniqueIds()) {
            playerDomain.addPlayer(uuid);
        }
        for (String name : domain.getPlayers()) {
            UUID uuid = (UUID)this.resolvedNames.get(name.toLowerCase());
            if (uuid != null) {
                playerDomain.addPlayer(uuid);
                continue;
            }
            if (this.keepUnresolvedNames) {
                playerDomain.addPlayer(name);
            }
            this.unresolvedNames.add(name);
        }
        domain.setPlayerDomain(playerDomain);
    }

    public boolean getKeepUnresolvedNames() {
        return this.keepUnresolvedNames;
    }

    public void setKeepUnresolvedNames(boolean keepUnresolvedNames) {
        this.keepUnresolvedNames = keepUnresolvedNames;
    }

    private class ResolvedNamesTimerTask
    extends TimerTask {
        private ResolvedNamesTimerTask() {
        }

        @Override
        public void run() {
            log.info("UUIDs have been found for " + UUIDMigration.this.resolvedNames.size() + " name(s)...");
        }
    }
}

