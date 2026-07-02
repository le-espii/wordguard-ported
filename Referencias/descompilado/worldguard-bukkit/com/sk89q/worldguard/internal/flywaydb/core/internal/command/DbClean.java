/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.command;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.callback.FlywayCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StopWatch;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.TimeFormat;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class DbClean {
    private static final Log LOG = LogFactory.getLog(DbClean.class);
    private final Connection connection;
    private final MetaDataTable metaDataTable;
    private final Schema[] schemas;
    private final FlywayCallback[] callbacks;

    public DbClean(Connection connection, MetaDataTable metaDataTable, Schema[] schemas, FlywayCallback[] callbacks) {
        this.connection = connection;
        this.metaDataTable = metaDataTable;
        this.schemas = schemas;
        this.callbacks = callbacks;
    }

    public void clean() throws FlywayException {
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connection).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.beforeClean(DbClean.this.connection);
                    return null;
                }
            });
        }
        boolean dropSchemas = false;
        try {
            dropSchemas = this.metaDataTable.hasSchemasMarker();
        }
        catch (Exception e) {
            LOG.error("Error while checking whether the schemas should be dropped", e);
        }
        for (Schema schema : this.schemas) {
            if (dropSchemas) {
                this.dropSchema(schema);
                continue;
            }
            this.cleanSchema(schema);
        }
        for (final FlywayCallback callback : this.callbacks) {
            new TransactionTemplate(this.connection).execute(new TransactionCallback<Object>(){

                @Override
                public Object doInTransaction() throws SQLException {
                    callback.afterClean(DbClean.this.connection);
                    return null;
                }
            });
        }
    }

    private void dropSchema(final Schema schema) {
        LOG.debug("Dropping schema " + schema + " ...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        new TransactionTemplate(this.connection).execute(new TransactionCallback<Void>(){

            @Override
            public Void doInTransaction() {
                schema.drop();
                return null;
            }
        });
        stopWatch.stop();
        LOG.info(String.format("Dropped schema %s (execution time %s)", schema, TimeFormat.format(stopWatch.getTotalTimeMillis())));
    }

    private void cleanSchema(final Schema schema) {
        LOG.debug("Cleaning schema " + schema + " ...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        new TransactionTemplate(this.connection).execute(new TransactionCallback<Void>(){

            @Override
            public Void doInTransaction() {
                schema.clean();
                return null;
            }
        });
        stopWatch.stop();
        LOG.info(String.format("Cleaned schema %s (execution time %s)", schema, TimeFormat.format(stopWatch.getTotalTimeMillis())));
    }
}

