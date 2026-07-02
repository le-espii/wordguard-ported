/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.command;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.api.callback.FlywayCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.AppliedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class DbInit {
    private static final Log LOG = LogFactory.getLog(DbInit.class);
    private final Connection connection;
    private final MetaDataTable metaDataTable;
    private final MigrationVersion initVersion;
    private final String initDescription;
    private final FlywayCallback[] callbacks;

    public DbInit(Connection connection, MetaDataTable metaDataTable, MigrationVersion initVersion, String initDescription, FlywayCallback[] callbacks) {
        this.connection = connection;
        this.metaDataTable = metaDataTable;
        this.initVersion = initVersion;
        this.initDescription = initDescription;
        this.callbacks = callbacks;
    }

    public void init() {
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connection).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.beforeInit(DbInit.this.connection);
                    return null;
                }
            });
        }
        new TransactionTemplate(this.connection).execute(new TransactionCallback<Void>(){

            @Override
            public Void doInTransaction() {
                if (DbInit.this.metaDataTable.hasAppliedMigrations()) {
                    throw new FlywayException("Unable to init metadata table " + DbInit.this.metaDataTable + " as it already contains migrations");
                }
                if (DbInit.this.metaDataTable.hasInitMarker()) {
                    AppliedMigration initMarker = DbInit.this.metaDataTable.getInitMarker();
                    if (DbInit.this.initVersion.equals(initMarker.getVersion()) && DbInit.this.initDescription.equals(initMarker.getDescription())) {
                        LOG.info("Metadata table " + DbInit.this.metaDataTable + " already initialized with (" + DbInit.this.initVersion + "," + DbInit.this.initDescription + "). Skipping.");
                        return null;
                    }
                    throw new FlywayException("Unable to init metadata table " + DbInit.this.metaDataTable + " with (" + DbInit.this.initVersion + "," + DbInit.this.initDescription + ") as it has already been initialized with (" + initMarker.getVersion() + "," + initMarker.getDescription() + ")");
                }
                if (DbInit.this.metaDataTable.hasSchemasMarker() && DbInit.this.initVersion.equals(MigrationVersion.fromVersion("0"))) {
                    throw new FlywayException("Unable to init metadata table " + DbInit.this.metaDataTable + " with version 0 as this version was used for schema creation");
                }
                DbInit.this.metaDataTable.addInitMarker(DbInit.this.initVersion, DbInit.this.initDescription);
                return null;
            }
        });
        LOG.info("Schema initialized with version: " + this.initVersion);
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connection).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.afterInit(DbInit.this.connection);
                    return null;
                }
            });
        }
    }
}

