/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.sql;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.MigrationInfoHelper;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.ResolvedMigrationComparator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.ResolvedMigrationImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.sql.SqlMigrationExecutor;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Location;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Pair;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.PlaceholderReplacer;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.CRC32;

public class SqlMigrationResolver
implements MigrationResolver {
    private final DbSupport dbSupport;
    private final Scanner scanner;
    private final Location location;
    private final PlaceholderReplacer placeholderReplacer;
    private final String encoding;
    private final String sqlMigrationPrefix;
    private final String sqlMigrationSeparator;
    private final String sqlMigrationSuffix;

    public SqlMigrationResolver(DbSupport dbSupport, ClassLoader classLoader, Location location, PlaceholderReplacer placeholderReplacer, String encoding, String sqlMigrationPrefix, String sqlMigrationSeparator, String sqlMigrationSuffix) {
        this.dbSupport = dbSupport;
        this.scanner = new Scanner(classLoader);
        this.location = location;
        this.placeholderReplacer = placeholderReplacer;
        this.encoding = encoding;
        this.sqlMigrationPrefix = sqlMigrationPrefix;
        this.sqlMigrationSeparator = sqlMigrationSeparator;
        this.sqlMigrationSuffix = sqlMigrationSuffix;
    }

    public List<ResolvedMigration> resolveMigrations() {
        ArrayList<ResolvedMigration> migrations = new ArrayList<ResolvedMigration>();
        try {
            Resource[] resources;
            for (Resource resource : resources = this.scanner.scanForResources(this.location, this.sqlMigrationPrefix, this.sqlMigrationSuffix)) {
                ResolvedMigrationImpl resolvedMigration = this.extractMigrationInfo(resource);
                resolvedMigration.setPhysicalLocation(resource.getLocationOnDisk());
                resolvedMigration.setExecutor(new SqlMigrationExecutor(this.dbSupport, resource, this.placeholderReplacer, this.encoding));
                migrations.add(resolvedMigration);
            }
        }
        catch (Exception e) {
            throw new FlywayException("Unable to scan for SQL migrations in location: " + this.location, e);
        }
        Collections.sort(migrations, new ResolvedMigrationComparator());
        return migrations;
    }

    private ResolvedMigrationImpl extractMigrationInfo(Resource resource) {
        ResolvedMigrationImpl migration = new ResolvedMigrationImpl();
        Pair<MigrationVersion, String> info = MigrationInfoHelper.extractVersionAndDescription(resource.getFilename(), this.sqlMigrationPrefix, this.sqlMigrationSeparator, this.sqlMigrationSuffix);
        migration.setVersion(info.getLeft());
        migration.setDescription(info.getRight());
        migration.setScript(this.extractScriptName(resource));
        migration.setChecksum(SqlMigrationResolver.calculateChecksum(resource.loadAsBytes()));
        migration.setType(MigrationType.SQL);
        return migration;
    }

    String extractScriptName(Resource resource) {
        if (this.location.getPath().isEmpty()) {
            return resource.getLocation();
        }
        return resource.getLocation().substring(this.location.getPath().length() + 1);
    }

    private static int calculateChecksum(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return (int)crc32.getValue();
    }
}

