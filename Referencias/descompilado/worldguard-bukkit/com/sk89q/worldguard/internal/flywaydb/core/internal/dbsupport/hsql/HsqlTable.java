/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.hsql;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.hsql.HsqlDbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;

public class HsqlTable
extends Table {
    private static final Log LOG = LogFactory.getLog(HsqlDbSupport.class);
    private boolean version18;

    public HsqlTable(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        super(jdbcTemplate, dbSupport, schema, name);
        try {
            int majorVersion = jdbcTemplate.getMetaData().getDatabaseMajorVersion();
            this.version18 = majorVersion < 2;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to determine the Hsql version", e);
        }
    }

    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP TABLE " + this.dbSupport.quote(this.schema.getName(), this.name) + " CASCADE", new Object[0]);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.exists(null, this.schema, this.name, new String[0]);
    }

    @Override
    protected void doLock() throws SQLException {
        if (this.version18) {
            LOG.debug("Unable to lock " + this + " as Hsql 1.8 does not support locking. No concurrent migration supported.");
        } else {
            this.jdbcTemplate.execute("LOCK TABLE " + this + " WRITE", new Object[0]);
        }
    }
}

