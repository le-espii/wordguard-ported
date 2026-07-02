/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.mysql;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import java.sql.SQLException;

public class MySQLTable
extends Table {
    public MySQLTable(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }

    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP TABLE " + this.dbSupport.quote(this.schema.getName(), this.name), new Object[0]);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.exists(this.schema, null, this.name, new String[0]);
    }

    @Override
    protected void doLock() throws SQLException {
        this.jdbcTemplate.execute("SELECT * FROM " + this + " FOR UPDATE", new Object[0]);
    }
}

