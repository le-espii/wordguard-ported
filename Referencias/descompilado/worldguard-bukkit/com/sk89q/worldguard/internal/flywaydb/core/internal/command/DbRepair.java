/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.command;

import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationInfo;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.callback.FlywayCallback;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationResolver;
import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.ResolvedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.info.MigrationInfoServiceImpl;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.AppliedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.ObjectUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import java.sql.Connection;
import java.sql.SQLException;

public class DbRepair {
    private final Connection connection;
    private final MigrationInfoServiceImpl migrationInfoService;
    private final MetaDataTable metaDataTable;
    private final FlywayCallback[] callbacks;

    public DbRepair(Connection connection, MigrationResolver migrationResolver, MetaDataTable metaDataTable, FlywayCallback[] callbacks) {
        this.connection = connection;
        this.migrationInfoService = new MigrationInfoServiceImpl(migrationResolver, metaDataTable, MigrationVersion.LATEST, true, true);
        this.metaDataTable = metaDataTable;
        this.callbacks = callbacks;
    }

    public void repair() {
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connection).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.beforeRepair(DbRepair.this.connection);
                    return null;
                }
            });
        }
        new TransactionTemplate(this.connection).execute(new TransactionCallback<Void>(){

            @Override
            public Void doInTransaction() {
                DbRepair.this.metaDataTable.removeFailedMigrations();
                DbRepair.this.migrationInfoService.refresh();
                for (MigrationInfo migrationInfo : DbRepair.this.migrationInfoService.all()) {
                    MigrationInfoImpl migrationInfoImpl = (MigrationInfoImpl)migrationInfo;
                    ResolvedMigration resolved = migrationInfoImpl.getResolvedMigration();
                    AppliedMigration applied = migrationInfoImpl.getAppliedMigration();
                    if (resolved == null || applied == null || ObjectUtils.nullSafeEquals(resolved.getChecksum(), applied.getChecksum())) continue;
                    DbRepair.this.metaDataTable.updateChecksum(migrationInfoImpl.getVersion(), resolved.getChecksum());
                }
                return null;
            }
        });
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connection).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.afterRepair(DbRepair.this.connection);
                    return null;
                }
            });
        }
    }
}

