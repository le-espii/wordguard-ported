/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.command;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.callback.FlywayCallback;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoServiceImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.Pair;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StopWatch;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.TimeFormat;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class DbValidate {
    private static final Log LOG = LogFactory.getLog(DbValidate.class);
    private final MigrationVersion target;
    private final MetaDataTable metaDataTable;
    private final MigrationResolver migrationResolver;
    private final Connection connectionMetaDataTable;
    private final Connection connectionUserObjects;
    private final boolean outOfOrder;
    private final boolean pending;
    private final FlywayCallback[] callbacks;

    public DbValidate(Connection connectionMetaDataTable, Connection connectionUserObjects, MetaDataTable metaDataTable, MigrationResolver migrationResolver, MigrationVersion target, boolean outOfOrder, boolean pending, FlywayCallback[] callbacks) {
        this.connectionMetaDataTable = connectionMetaDataTable;
        this.connectionUserObjects = connectionUserObjects;
        this.metaDataTable = metaDataTable;
        this.migrationResolver = migrationResolver;
        this.target = target;
        this.outOfOrder = outOfOrder;
        this.pending = pending;
        this.callbacks = callbacks;
    }

    public String validate() {
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connectionUserObjects).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.beforeValidate(DbValidate.this.connectionUserObjects);
                    return null;
                }
            });
        }
        LOG.debug("Validating migrations ...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Pair<Integer, String> result = new TransactionTemplate(this.connectionMetaDataTable).execute(new TransactionCallback<Pair<Integer, String>>(){

            @Override
            public Pair<Integer, String> doInTransaction() {
                MigrationInfoServiceImpl migrationInfoService = new MigrationInfoServiceImpl(DbValidate.this.migrationResolver, DbValidate.this.metaDataTable, DbValidate.this.target, DbValidate.this.outOfOrder, DbValidate.this.pending);
                migrationInfoService.refresh();
                int count = migrationInfoService.all().length;
                String validationError = migrationInfoService.validate();
                return Pair.of(count, validationError);
            }
        });
        stopWatch.stop();
        int count = result.getLeft();
        if (count == 1) {
            LOG.info(String.format("Validated 1 migration (execution time %s)", TimeFormat.format(stopWatch.getTotalTimeMillis())));
        } else {
            LOG.info(String.format("Validated %d migrations (execution time %s)", count, TimeFormat.format(stopWatch.getTotalTimeMillis())));
        }
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connectionUserObjects).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.afterValidate(DbValidate.this.connectionUserObjects);
                    return null;
                }
            });
        }
        return result.getRight();
    }
}

