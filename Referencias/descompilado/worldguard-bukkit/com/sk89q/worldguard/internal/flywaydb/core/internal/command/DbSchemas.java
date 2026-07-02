/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.command;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;

public class DbSchemas {
    private static final Log LOG = LogFactory.getLog(DbSchemas.class);
    private final Connection connection;
    private final Schema[] schemas;
    private final MetaDataTable metaDataTable;

    public DbSchemas(Connection connection, Schema[] schemas, MetaDataTable metaDataTable) {
        this.connection = connection;
        this.schemas = schemas;
        this.metaDataTable = metaDataTable;
    }

    public void create() {
        new TransactionTemplate(this.connection).execute(new TransactionCallback<Void>(){

            @Override
            public Void doInTransaction() {
                for (Schema schema : DbSchemas.this.schemas) {
                    if (!schema.exists()) continue;
                    LOG.debug("Schema " + schema + " already exists. Skipping schema creation.");
                    return null;
                }
                for (Schema schema : DbSchemas.this.schemas) {
                    LOG.info("Creating schema " + schema + " ...");
                    schema.create();
                }
                DbSchemas.this.metaDataTable.addSchemasMarker(DbSchemas.this.schemas);
                return null;
            }
        });
    }
}

