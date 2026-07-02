/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.ResolvedMigrationComparator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.jdbc.JdbcMigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.spring.SpringJdbcMigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.sql.SqlMigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.FeatureDetector;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Location;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Locations;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.PlaceholderReplacer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class CompositeMigrationResolver
implements MigrationResolver {
    private Collection<MigrationResolver> migrationResolvers = new ArrayList<MigrationResolver>();
    private List<ResolvedMigration> availableMigrations;

    public CompositeMigrationResolver(DbSupport dbSupport, ClassLoader classLoader, Locations locations, String encoding, String sqlMigrationPrefix, String sqlMigrationSeparator, String sqlMigrationSuffix, PlaceholderReplacer placeholderReplacer, MigrationResolver ... customMigrationResolvers) {
        for (Location location : locations.getLocations()) {
            this.migrationResolvers.add(new SqlMigrationResolver(dbSupport, classLoader, location, placeholderReplacer, encoding, sqlMigrationPrefix, sqlMigrationSeparator, sqlMigrationSuffix));
            this.migrationResolvers.add(new JdbcMigrationResolver(classLoader, location));
            if (!new FeatureDetector(classLoader).isSpringJdbcAvailable()) continue;
            this.migrationResolvers.add(new SpringJdbcMigrationResolver(classLoader, location));
        }
        this.migrationResolvers.addAll(Arrays.asList(customMigrationResolvers));
    }

    public List<ResolvedMigration> resolveMigrations() {
        if (this.availableMigrations == null) {
            this.availableMigrations = this.doFindAvailableMigrations();
        }
        return this.availableMigrations;
    }

    private List<ResolvedMigration> doFindAvailableMigrations() throws FlywayException {
        ArrayList<ResolvedMigration> migrations = new ArrayList<ResolvedMigration>(CompositeMigrationResolver.collectMigrations(this.migrationResolvers));
        Collections.sort(migrations, new ResolvedMigrationComparator());
        CompositeMigrationResolver.checkForIncompatibilities(migrations);
        return migrations;
    }

    static Collection<ResolvedMigration> collectMigrations(Collection<MigrationResolver> migrationResolvers) {
        HashSet<ResolvedMigration> migrations = new HashSet<ResolvedMigration>();
        for (MigrationResolver migrationResolver : migrationResolvers) {
            migrations.addAll(migrationResolver.resolveMigrations());
        }
        return migrations;
    }

    static void checkForIncompatibilities(List<ResolvedMigration> migrations) {
        for (int i = 0; i < migrations.size() - 1; ++i) {
            ResolvedMigration current = migrations.get(i);
            ResolvedMigration next = migrations.get(i + 1);
            if (current.getVersion().compareTo(next.getVersion()) != 0) continue;
            throw new FlywayException(String.format("Found more than one migration with version '%s' (Offenders: %s '%s' and %s '%s')", new Object[]{current.getVersion(), current.getType(), current.getPhysicalLocation(), next.getType(), next.getPhysicalLocation()}));
        }
    }
}

