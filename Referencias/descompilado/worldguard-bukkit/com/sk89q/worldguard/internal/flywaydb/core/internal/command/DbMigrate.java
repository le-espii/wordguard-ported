/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.command;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationInfo;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationState;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.callback.FlywayCallback;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationExecutor;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoServiceImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.AppliedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StopWatch;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.TimeFormat;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class DbMigrate {
    private static final Log LOG = LogFactory.getLog(DbMigrate.class);
    private final MigrationVersion target;
    private final DbSupport dbSupport;
    private final MetaDataTable metaDataTable;
    private final Schema schema;
    private final MigrationResolver migrationResolver;
    private final Connection connectionMetaDataTable;
    private final Connection connectionUserObjects;
    private final boolean ignoreFailedFutureMigration;
    private final boolean outOfOrder;
    private final FlywayCallback[] callbacks;

    public DbMigrate(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, MetaDataTable metaDataTable, Schema schema, MigrationResolver migrationResolver, MigrationVersion target, boolean ignoreFailedFutureMigration, boolean outOfOrder, FlywayCallback[] callbacks) {
        this.connectionMetaDataTable = connectionMetaDataTable;
        this.connectionUserObjects = connectionUserObjects;
        this.dbSupport = dbSupport;
        this.metaDataTable = metaDataTable;
        this.schema = schema;
        this.migrationResolver = migrationResolver;
        this.target = target;
        this.ignoreFailedFutureMigration = ignoreFailedFutureMigration;
        this.outOfOrder = outOfOrder;
        this.callbacks = callbacks;
    }

    public int migrate() throws FlywayException {
        boolean firstRun;
        MigrationVersion result;
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connectionUserObjects).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.beforeMigrate(DbMigrate.this.connectionUserObjects);
                    return null;
                }
            });
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int migrationSuccessCount = 0;
        while ((result = new TransactionTemplate(this.connectionMetaDataTable, false).execute(new TransactionCallback<MigrationVersion>(firstRun = migrationSuccessCount == 0){
            final /* synthetic */ boolean val$firstRun;
            {
                this.val$firstRun = bl;
            }

            @Override
            public MigrationVersion doInTransaction() {
                MigrationInfoImpl[] pendingMigrations;
                MigrationInfo[] failed;
                MigrationInfo[] future;
                DbMigrate.this.metaDataTable.lock();
                MigrationInfoServiceImpl infoService = new MigrationInfoServiceImpl(DbMigrate.this.migrationResolver, DbMigrate.this.metaDataTable, DbMigrate.this.target, DbMigrate.this.outOfOrder, true);
                infoService.refresh();
                MigrationVersion currentSchemaVersion = MigrationVersion.EMPTY;
                if (infoService.current() != null) {
                    currentSchemaVersion = infoService.current().getVersion();
                }
                if (this.val$firstRun) {
                    LOG.info("Current version of schema " + DbMigrate.this.schema + ": " + currentSchemaVersion);
                    if (DbMigrate.this.outOfOrder) {
                        LOG.warn("outOfOrder mode is active. Migration of schema " + DbMigrate.this.schema + " may not be reproducible.");
                    }
                }
                if ((future = infoService.future()).length > 0) {
                    MigrationInfo[] resolved = infoService.resolved();
                    if (resolved.length == 0) {
                        LOG.warn("Schema " + DbMigrate.this.schema + " has version " + currentSchemaVersion + ", but no migration could be resolved in the configured locations !");
                    } else {
                        LOG.warn("Schema " + DbMigrate.this.schema + " has a version (" + currentSchemaVersion + ") that is newer than the latest available migration (" + resolved[resolved.length - 1].getVersion() + ") !");
                    }
                }
                if ((failed = infoService.failed()).length > 0) {
                    if (failed.length == 1 && failed[0].getState() == MigrationState.FUTURE_FAILED && DbMigrate.this.ignoreFailedFutureMigration) {
                        LOG.warn("Schema " + DbMigrate.this.schema + " contains a failed future migration to version " + failed[0].getVersion() + " !");
                    } else {
                        throw new FlywayException("Schema " + DbMigrate.this.schema + " contains a failed migration to version " + failed[0].getVersion() + " !");
                    }
                }
                if ((pendingMigrations = infoService.pending()).length == 0) {
                    return null;
                }
                boolean isOutOfOrder = pendingMigrations[0].getVersion().compareTo(currentSchemaVersion) < 0;
                return DbMigrate.this.applyMigration(pendingMigrations[0], isOutOfOrder);
            }
        })) != null) {
            ++migrationSuccessCount;
        }
        stopWatch.stop();
        this.logSummary(migrationSuccessCount, stopWatch.getTotalTimeMillis());
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connectionUserObjects).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.afterMigrate(DbMigrate.this.connectionUserObjects);
                    return null;
                }
            });
        }
        return migrationSuccessCount;
    }

    private void logSummary(int migrationSuccessCount, long executionTime) {
        if (migrationSuccessCount == 0) {
            LOG.info("Schema " + this.schema + " is up to date. No migration necessary.");
            return;
        }
        if (migrationSuccessCount == 1) {
            LOG.info("Successfully applied 1 migration to schema " + this.schema + " (execution time " + TimeFormat.format(executionTime) + ").");
        } else {
            LOG.info("Successfully applied " + migrationSuccessCount + " migrations to schema " + this.schema + " (execution time " + TimeFormat.format(executionTime) + ").");
        }
    }

    private MigrationVersion applyMigration(final MigrationInfoImpl migration, boolean isOutOfOrder) {
        MigrationVersion version = migration.getVersion();
        if (isOutOfOrder) {
            LOG.info("Migrating schema " + this.schema + " to version " + version + " (out of order)");
        } else {
            LOG.info("Migrating schema " + this.schema + " to version " + version);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            for (final FlywayCallback callback : this.callbacks) {
                new TransactionTemplate(this.connectionUserObjects).execute(new TransactionCallback<Object>(){

                    @Override
                    public Object doInTransaction() throws SQLException {
                        callback.beforeEachMigrate(DbMigrate.this.connectionUserObjects, migration);
                        return null;
                    }
                });
            }
            final MigrationExecutor migrationExecutor = migration.getResolvedMigration().getExecutor();
            if (migrationExecutor.executeInTransaction()) {
                new TransactionTemplate(this.connectionUserObjects).execute(new TransactionCallback<Void>(){

                    @Override
                    public Void doInTransaction() throws SQLException {
                        migrationExecutor.execute(DbMigrate.this.connectionUserObjects);
                        return null;
                    }
                });
            } else {
                try {
                    migrationExecutor.execute(this.connectionUserObjects);
                }
                catch (SQLException e) {
                    throw new FlywayException("Unable to apply migration", e);
                }
            }
            LOG.debug("Successfully completed and committed migration of schema " + this.schema + " to version " + version);
            for (final FlywayCallback callback : this.callbacks) {
                new TransactionTemplate(this.connectionUserObjects).execute(new TransactionCallback<Object>(){

                    @Override
                    public Object doInTransaction() throws SQLException {
                        callback.afterEachMigrate(DbMigrate.this.connectionUserObjects, migration);
                        return null;
                    }
                });
            }
        }
        catch (FlywayException e) {
            String failedMsg = "Migration of schema " + this.schema + " to version " + version + " failed!";
            if (this.dbSupport.supportsDdlTransactions()) {
                LOG.error(failedMsg + " Changes successfully rolled back.");
            } else {
                LOG.error(failedMsg + " Please restore backups and roll back database and code!");
                stopWatch.stop();
                int executionTime = (int)stopWatch.getTotalTimeMillis();
                AppliedMigration appliedMigration = new AppliedMigration(version, migration.getDescription(), migration.getType(), migration.getScript(), migration.getChecksum(), executionTime, false);
                this.metaDataTable.addAppliedMigration(appliedMigration);
            }
            throw e;
        }
        stopWatch.stop();
        int executionTime = (int)stopWatch.getTotalTimeMillis();
        AppliedMigration appliedMigration = new AppliedMigration(version, migration.getDescription(), migration.getType(), migration.getScript(), migration.getChecksum(), executionTime, true);
        this.metaDataTable.addAppliedMigration(appliedMigration);
        return version;
    }
}

