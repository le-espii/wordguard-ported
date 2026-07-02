/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.jdbc;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.migration.MigrationChecksumProvider;
import com.sk89q.worldguard.internal.flywaydb.core.api.migration.MigrationInfoProvider;
import com.sk89q.worldguard.internal.flywaydb.core.api.migration.jdbc.JdbcMigration;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.MigrationInfoHelper;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.ResolvedMigrationComparator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.ResolvedMigrationImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.jdbc.JdbcMigrationExecutor;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.ClassUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Location;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Pair;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JdbcMigrationResolver
implements MigrationResolver {
    private final Location location;
    private ClassLoader classLoader;

    public JdbcMigrationResolver(ClassLoader classLoader, Location location) {
        this.location = location;
        this.classLoader = classLoader;
    }

    public List<ResolvedMigration> resolveMigrations() {
        ArrayList<ResolvedMigration> migrations = new ArrayList<ResolvedMigration>();
        if (!this.location.isClassPath()) {
            return migrations;
        }
        try {
            Class<?>[] classes;
            for (Class<?> clazz : classes = new Scanner(this.classLoader).scanForClasses(this.location, JdbcMigration.class)) {
                JdbcMigration jdbcMigration = (JdbcMigration)ClassUtils.instantiate(clazz.getName(), this.classLoader);
                ResolvedMigrationImpl migrationInfo = this.extractMigrationInfo(jdbcMigration);
                migrationInfo.setPhysicalLocation(ClassUtils.getLocationOnDisk(clazz));
                migrationInfo.setExecutor(new JdbcMigrationExecutor(jdbcMigration));
                migrations.add(migrationInfo);
            }
        }
        catch (Exception e) {
            throw new FlywayException("Unable to resolve Jdbc Java migrations in location: " + this.location, e);
        }
        Collections.sort(migrations, new ResolvedMigrationComparator());
        return migrations;
    }

    ResolvedMigrationImpl extractMigrationInfo(JdbcMigration jdbcMigration) {
        String description;
        MigrationVersion version;
        Integer checksum = null;
        if (jdbcMigration instanceof MigrationChecksumProvider) {
            MigrationChecksumProvider checksumProvider = (MigrationChecksumProvider)((Object)jdbcMigration);
            checksum = checksumProvider.getChecksum();
        }
        if (jdbcMigration instanceof MigrationInfoProvider) {
            MigrationInfoProvider infoProvider = (MigrationInfoProvider)((Object)jdbcMigration);
            version = infoProvider.getVersion();
            description = infoProvider.getDescription();
            if (!StringUtils.hasText(description)) {
                throw new FlywayException("Missing description for migration " + version);
            }
        } else {
            Pair<MigrationVersion, String> info = MigrationInfoHelper.extractVersionAndDescription(ClassUtils.getShortName(jdbcMigration.getClass()), "V", "__", "");
            version = info.getLeft();
            description = info.getRight();
        }
        String script = jdbcMigration.getClass().getName();
        ResolvedMigrationImpl resolvedMigration = new ResolvedMigrationImpl();
        resolvedMigration.setVersion(version);
        resolvedMigration.setDescription(description);
        resolvedMigration.setScript(script);
        resolvedMigration.setChecksum(checksum);
        resolvedMigration.setType(MigrationType.JDBC);
        return resolvedMigration;
    }
}

