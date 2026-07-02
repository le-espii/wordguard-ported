/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlite;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;

public class SQLiteTable
extends Table {
    private static final Log LOG = LogFactory.getLog(SQLiteTable.class);

    public SQLiteTable(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }

    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP TABLE " + this.dbSupport.quote(this.schema.getName(), this.name), new Object[0]);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT count(tbl_name) FROM " + this.dbSupport.quote(this.schema.getName()) + ".sqlite_master WHERE type='table' AND tbl_name='" + this.name + "'", new String[0]) > 0;
    }

    @Override
    protected void doLock() throws SQLException {
        LOG.debug("Unable to lock " + this + " as SQLite does not support locking. No concurrent migration supported.");
    }
}

