/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2.DB2Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2.DB2SqlStatementBuilder;
import java.sql.Connection;
import java.sql.SQLException;

public class DB2DbSupport
extends DbSupport {
    public DB2DbSupport(Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }

    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new DB2SqlStatementBuilder();
    }

    @Override
    public String getDbName() {
        return "db2";
    }

    @Override
    protected String doGetCurrentSchema() throws SQLException {
        return this.jdbcTemplate.queryForString("select current_schema from sysibm.sysdummy1", new String[0]);
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        this.jdbcTemplate.execute("SET SCHEMA " + schema, new Object[0]);
    }

    @Override
    public String getCurrentUserFunction() {
        return "CURRENT_USER";
    }

    @Override
    public boolean supportsDdlTransactions() {
        return true;
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
    public String doQuote(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    public Schema getSchema(String name) {
        return new DB2Schema(this.jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}

