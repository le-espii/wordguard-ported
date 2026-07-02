/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.oracle;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.oracle.OracleSchema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.oracle.OracleSqlStatementBuilder;
import java.sql.Connection;
import java.sql.SQLException;

public class OracleDbSupport
extends DbSupport {
    public OracleDbSupport(Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }

    @Override
    public String getDbName() {
        return "oracle";
    }

    @Override
    public String getCurrentUserFunction() {
        return "USER";
    }

    @Override
    protected String doGetCurrentSchema() throws SQLException {
        return this.jdbcTemplate.queryForString("SELECT USER FROM dual", new String[0]);
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        this.jdbcTemplate.execute("ALTER SESSION SET CURRENT_SCHEMA=" + schema, new Object[0]);
    }

    @Override
    public boolean supportsDdlTransactions() {
        return false;
    }

    @Override
    public String getBooleanTrue() {
        return "1";
    }

    @Override
    public String getBooleanFalse() {
        return "0";
    }

    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new OracleSqlStatementBuilder();
    }

    @Override
    public String doQuote(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    public Schema getSchema(String name) {
        return new OracleSchema(this.jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}

