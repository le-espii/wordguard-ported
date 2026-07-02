/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.derby;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.derby.DerbySchema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.derby.DerbySqlStatementBuilder;
import java.sql.Connection;
import java.sql.SQLException;

public class DerbyDbSupport
extends DbSupport {
    public DerbyDbSupport(Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }

    @Override
    public String getDbName() {
        return "derby";
    }

    @Override
    public String getCurrentUserFunction() {
        return "CURRENT_USER";
    }

    @Override
    protected String doGetCurrentSchema() throws SQLException {
        return this.jdbcTemplate.queryForString("SELECT CURRENT SCHEMA FROM SYSIBM.SYSDUMMY1", new String[0]);
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        this.jdbcTemplate.execute("SET SCHEMA " + schema, new Object[0]);
    }

    @Override
    public boolean supportsDdlTransactions() {
        return true;
    }

    @Override
    public String getBooleanTrue() {
        return "true";
    }

    @Override
    public String getBooleanFalse() {
        return "false";
    }

    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new DerbySqlStatementBuilder();
    }

    @Override
    public String doQuote(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    public Schema getSchema(String name) {
        return new DerbySchema(this.jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}

